package jvn.SearchService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Owner {

    @Id
    @Positive(message = "Id must be positive.")
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Name is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ]+[.]?[ ]?|[a-zÀ-ƒ]+['-]?){0,4})$", message = "Name is not valid.")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Email is empty.")
    @Email(message = "Email is not valid.")
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Advertisement> advertisements = new HashSet<>();

}
