package lu.kbra.pclib.db.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

@Component
public class ConversionServiceHolder {

	private static ConversionService conversionService;

	public ConversionServiceHolder(@Autowired(required = false) ConversionService cs, ConfigurableEnvironment env) {
		if (cs != null) {
			conversionService = cs;
		} else {
			conversionService = env.getConversionService();
		}
	}

	public static ConversionService get() {
		return conversionService;
	}

	public static <T> T convert(Object source, Class<T> targetType) {
		return conversionService.convert(source, targetType);
	}

}
