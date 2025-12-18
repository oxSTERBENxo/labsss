package mk.ukim.finki.wp.kol2025g2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class SkiResort {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String location;

    public SkiResort(String name, String location) {
        this.name = name;
        this.location = location;
    }


}
