package roomescape.theme.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ThemeService {

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public List<Theme> findAll() {
        return themeRepository.findAll();
    }

    @Transactional
    public Theme save(Theme theme) {
        return themeRepository.save(theme);
    }

    @Transactional
    public void deleteById(Long id) {
        Theme theme = themeRepository.getThemeById(id);
        theme.delete();
    }
}
