// This file was automatically generated.
package com.example.filmtinder.db.genre;

import com.example.filmtinder.db.DBEntityDto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Generated
public class GenreDto implements DBEntityDto {

    @NotBlank
    private String name;

}

    