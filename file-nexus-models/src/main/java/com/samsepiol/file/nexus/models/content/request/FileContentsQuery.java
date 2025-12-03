package com.samsepiol.file.nexus.models.content.request;

import com.samsepiol.file.nexus.models.validation.NotFutureDate;
import com.samsepiol.file.nexus.models.validation.ValidYYYYMMDDDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class FileContentsQuery {
    @NotNull
    @ValidYYYYMMDDDate
    @NotFutureDate
    String date;

    @NotNull
    Map<String, String> filters;
}
