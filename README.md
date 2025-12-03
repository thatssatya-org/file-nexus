# File Nexus

This document provides an overview of the File Nexus project, including key components, recent improvements, and implementation details.

## Table of Contents
1. [Storage Hook Improvements](#storage-hook-improvements)
2. [File State DAO Migration](#file-state-dao-migration)
3. [Configuration](#configuration)
4. [Usage Examples](#usage-examples)
5. [Future Enhancements](#future-enhancements)

## Storage Hook Improvements

### Overview

The storage hooks have been enhanced to:
1. Use `IFileStorageClient.listFiles()` instead of `getAllFiles()`
2. Support pagination for handling large numbers of files
3. Implement configurable file filtering to skip unnecessary files
4. Track timestamps to avoid reprocessing files after pod restarts
5. Reduce unnecessary SMTP notifications

### Key Components

#### 1. FileInfo Model
- **Location**: `file-nexus-service/src/main/java/com/samsepiol/file/nexus/storage/models/FileInfo.java`
- **Purpose**: Represents file metadata including path, last modified time, size, and etag
- **Usage**: Used by storage hooks to return structured file information

#### 2. FileFilterConfig
- **Location**: `file-nexus-service/src/main/java/com/samsepiol/file/nexus/storage/config/FileFilterConfig.java`
- **Purpose**: Configuration for file filtering behavior
- **Features**:
  - Skip patterns (e.g., "*.tmp", "*.log")
  - Minimum file age requirement
  - Page size for pagination
  - Timestamp filtering enable/disable

#### 3. TimestampTrackingService
- **Location**: `file-nexus-service/src/main/java/com/samsepiol/file/nexus/storage/service/TimestampTrackingService.java`
- **Purpose**: Tracks last processed timestamps to avoid reprocessing files
- **Implementation**: Uses Redis for persistence

#### 4. FileFilterUtil
- **Location**: `file-nexus-service/src/main/java/com/samsepiol/file/nexus/storage/util/FileFilterUtil.java`
- **Purpose**: Utility for filtering files based on configuration
- **Features**:
  - Pattern matching with glob support
  - Timestamp-based filtering
  - Minimum age filtering

### Implementation Changes

#### AbstractStorageHook
- **Changed**: `getAllFiles()` → `listFiles(Instant lastProcessedTime, int maxResults)`
- **Added**: File filtering logic in `poll()` method
- **Added**: Timestamp tracking integration
- **Added**: Configuration-based filtering support

#### GCSHook & S3Hook
- **Updated**: Constructor to include `TimestampTrackingService` and `StorageHookConfig`
- **Implemented**: New `listFiles()` method

### Benefits

1. **Efficient Processing**: Only processes new/modified files
2. **Pod Restart Resilience**: Maintains state across restarts via timestamp tracking
3. **Configurable Filtering**: Avoids unnecessary SMTP notifications for temp files
4. **Scalable**: Handles large numbers of files through pagination
5. **Cloud-Agnostic**: Works with both S3 and GCS

## File State DAO Migration

### Overview
Successfully replaced the in-memory `Map<String, String> fileStates = new ConcurrentHashMap<>()` in `AbstractStorageHook` with a proper DAO pattern using database persistence.

### Changes Made

#### 1. Created New Enum
- **File**: `file-nexus-repository/src/main/java/com/samsepiol/file/nexus/enums/FileStateStatus.java`
- **Purpose**: Defines file state statuses (`IN_PROCESS`, `DONE`)
- **Replaces**: String constants for file states

#### 2. Created Entity
- **File**: `file-nexus-repository/src/main/java/com/samsepiol/file/nexus/repo/content/entity/FileStateEntity.java`
- **Purpose**: Database entity for storing file states
- **Fields**:
  - `filePath`: The path of the file
  - `hookName`: Name of the storage hook
  - `status`: File state status (enum)
  - `stateKey`: Unique key for the file state
  - Inherits `id`, `createdAt`, `updatedAt` from base `Entity`

#### 3. Created Repository Layer
- **Interface**: `file-nexus-repository/src/main/java/com/samsepiol/file/nexus/repo/content/FileStateRepository.java`
- **Implementation**: `file-nexus-repository/src/main/java/com/samsepiol/file/nexus/repo/content/impl/DefaultFileStateRepository.java`
- **Request Models**:
  - `FileStateFetchRepositoryRequest.java`
  - `FileStateSaveRepositoryRequest.java`
- **Operations**:
  - `fetch()`: Get file state by state key
  - `save()`: Save/update file state
  - `deleteByStateKey()`: Delete file state

#### 4. Created Service Layer
- **File**: `file-nexus-service/src/main/java/com/samsepiol/file/nexus/storage/service/FileStateService.java`
- **Purpose**: High-level service for file state operations
- **Methods**:
  - `getFileState(String stateKey)`: Returns formatted state string
  - `setFileState(String filePath, String hookName, String stateKey, String state)`: Saves file state
  - `deleteFileState(String stateKey)`: Deletes file state
- **Features**:
  - Converts between string states and enum values
  - Handles database exceptions gracefully
  - Provides logging for debugging

#### 5. Updated AbstractStorageHook
- **File**: `file-nexus-service/src/main/java/com/samsepiol/file/nexus/storage/hook/AbstractStorageHook.java`
- **Changes**:
  - Removed `private final Map<String, String> fileStates = new ConcurrentHashMap<>()`
  - Added `FileStateService` dependency
  - Updated `getFileState()` to use service
  - Updated `setFileState()` to use service
  - Removed unused imports

#### 6. Created Tests
- **File**: `file-nexus-service/src/test/java/com/samsepiol/file/nexus/storage/service/FileStateServiceTest.java`
- **Coverage**: Tests all service methods with various scenarios
- **Test Cases**:
  - Get existing file state
  - Get non-existent file state
  - Set file state with different statuses
  - Delete file state
  - Handle database exceptions

### Benefits

#### 1. Persistence
- File states are now persisted in the database
- Survives application restarts
- Provides audit trail with timestamps

#### 2. Scalability
- No memory limitations for file state tracking
- Supports distributed deployments
- Database-level concurrency control

#### 3. Observability
- File states can be queried directly from database
- Audit trail with creation and update timestamps
- Better debugging capabilities

#### 4. Data Integrity
- Structured data with proper types
- Database constraints and validation
- Consistent state management

### Database Schema
The implementation uses MongoDB with the following collection:

**Collection**: `file_states`
```json
{
  "_id": "uuid",
  "createdAt": 1234567890,
  "updatedAt": 1234567890,
  "filePath": "/path/to/file.txt",
  "hookName": "storage-hook-name",
  "status": "IN_PROCESS",
  "stateKey": "nexus:file-state:hook-name:/path/to/file.txt"
}
```

## Configuration

### Application Configuration (application.yml)

```yaml
nexus:
  storages:
    - name: embosser-file-source
      type: s3
      bucket: uni-input
      fileFiltering:
        skipPatterns:
          - "*.tmp"
          - "*.log"
          - ".DS_Store"
          - "*.swp"
        minFileAge: PT5M  # Skip files newer than 5 minutes
        pageSize: 1000
        timestampFilteringEnabled: true
        timestampKeyPrefix: "nexus:last-processed:"
      destinations:
        - type: SMTP
          # ... SMTP configuration
```

### Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `skipPatterns` | List of file patterns to skip | `null` |
| `minFileAge` | Minimum age of files to process | `PT5M` (5 minutes) |
| `pageSize` | Maximum files per listFiles call | `1000` |
| `timestampFilteringEnabled` | Enable timestamp-based filtering | `true` |
| `timestampKeyPrefix` | Redis key prefix for timestamps | `"nexus:last-processed:"` |

## Usage Examples

### Skip Temporary Files
```yaml
fileFiltering:
  skipPatterns:
    - "*.tmp"
    - "*.swp"
    - ".DS_Store"
```

### Process Only Recent Files
```yaml
fileFiltering:
  minFileAge: PT10M  # Only process files older than 10 minutes
  timestampFilteringEnabled: true
```

### Large Bucket Optimization
```yaml
fileFiltering:
  pageSize: 500  # Smaller page size for very large buckets
```

## Future Enhancements

1. **Database Indexes**: Add index on `stateKey` for better query performance
2. **Cleanup Job**: Implement periodic cleanup of old file states
3. **Metrics**: Add metrics for file state operations and file processing efficiency
4. **Caching**: Add Redis cache layer for frequently accessed states
5. **Bulk Operations**: Support bulk state updates for better performance
6. **Add configuration validation** for filter patterns
