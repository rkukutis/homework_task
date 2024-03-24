package com.rhoopoe.myfashiontrunk.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rhoopoe.myfashiontrunk.enumerated.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CategoryDTO {

    @NotBlank(message = "Category name is required")
    @Length(max = 100, message = "Category name must less than 100 characters long")
    private String name;

    @NotNull
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private CategoryType type;

    @NotNull
    @NotEmpty(message = "At least one alias must be specified")
    private List<String> aliases;
}
