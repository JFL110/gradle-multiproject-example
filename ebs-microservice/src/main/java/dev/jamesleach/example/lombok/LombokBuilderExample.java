package dev.jamesleach.example.lombok;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class LombokBuilderExample {
    private String name;
    @NonNull  private String value;
}
