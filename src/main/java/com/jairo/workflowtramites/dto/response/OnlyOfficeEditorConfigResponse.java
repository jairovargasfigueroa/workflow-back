package com.jairo.workflowtramites.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnlyOfficeEditorConfigResponse {

    private String documentServerUrl;
    private Map<String, Object> config;
    private String token;
}
