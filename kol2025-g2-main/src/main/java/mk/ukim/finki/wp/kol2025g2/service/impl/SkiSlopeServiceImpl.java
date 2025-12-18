package mk.ukim.finki.wp.kol2025g2.service.impl;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;
import mk.ukim.finki.wp.kol2025g2.model.SkiResort;
import mk.ukim.finki.wp.kol2025g2.model.SkiSlope;
import mk.ukim.finki.wp.kol2025g2.model.SlopeDifficulty;
import mk.ukim.finki.wp.kol2025g2.model.exceptions.InvalidSkiResortIdException;
import mk.ukim.finki.wp.kol2025g2.model.exceptions.InvalidSkiSlopeIdException;
import mk.ukim.finki.wp.kol2025g2.repository.SkiResortRepository;
import mk.ukim.finki.wp.kol2025g2.repository.SkiSlopeRepository;
import mk.ukim.finki.wp.kol2025g2.service.SkiSlopeService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkiSlopeServiceImpl implements SkiSlopeService {

    private final SkiSlopeRepository skiSlopeRepository;
    private final SkiResortRepository skiResortRepository;

    public SkiSlopeServiceImpl(SkiSlopeRepository skiSlopeRepository, SkiResortRepository skiResortRepository) {
        this.skiSlopeRepository = skiSlopeRepository;
        this.skiResortRepository = skiResortRepository;
    }

    @Override
    public List<SkiSlope> listAll() {
        return skiSlopeRepository.findAll();
    }

    @Override
    public SkiSlope findById(Long id) {
        return skiSlopeRepository.findById(id).orElseThrow(InvalidSkiSlopeIdException::new);
    }

    @Override
    public SkiSlope create(String name, Integer length, SlopeDifficulty difficulty, Long skiResort) {
        SkiResort resort = skiResortRepository.findById(skiResort)
                .orElseThrow(InvalidSkiResortIdException::new);

        SkiSlope skiSlope = new SkiSlope(name, length, difficulty, resort);
        skiSlope.setClosed(false);

        return skiSlopeRepository.save(skiSlope);
    }


    @Override
    public SkiSlope update(Long id, String name, Integer length, SlopeDifficulty difficulty, Long skiResort) {
        SkiSlope slope = findById(id);
        SkiResort resort = skiResortRepository.findById(skiResort).orElseThrow(InvalidSkiResortIdException::new);

        slope.setName(name);
        slope.setLength(length);
        slope.setDifficulty(difficulty);
        slope.setSkiResort(resort);

        return skiSlopeRepository.save(slope);
    }

    @Override
    public SkiSlope delete(Long id) {
        SkiSlope skiSlope = findById(id);
        skiSlopeRepository.deleteById(id);
        return skiSlope;
    }

    @Override
    public SkiSlope close(Long id) {
        SkiSlope slope = findById(id);
        slope.setClosed(true);
        return skiSlopeRepository.save(slope);
    }

    @Override
    public Page<SkiSlope> findPage(String name, Integer length, SlopeDifficulty difficulty, Long skiResort,
                                   int pageNum, int pageSize) {

        List<SkiSlope> filtered = skiSlopeRepository.findAll().stream()
                .filter(s -> name == null || name.isBlank() || s.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(s -> length == null || s.getLength() > length) // "longer than"
                .filter(s -> difficulty == null || s.getDifficulty() == difficulty)
                .filter(s -> skiResort == null || (s.getSkiResort() != null && s.getSkiResort().getId().equals(skiResort)))
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(pageNum, pageSize);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());

        List<SkiSlope> content = start >= filtered.size() ? List.of() : filtered.subList(start, end);

        return new PageImpl<>(content, pageable, filtered.size());
    }

}
