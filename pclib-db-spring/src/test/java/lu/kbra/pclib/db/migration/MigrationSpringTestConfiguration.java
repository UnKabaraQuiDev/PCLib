package lu.kbra.pclib.db.migration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = MigrationSpringTestConfiguration.class)
public class MigrationSpringTestConfiguration {
}
