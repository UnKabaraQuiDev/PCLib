package lu.kbra.pclib.db.config;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component
public class ConversionServiceHolder {

	private static ConversionService conversionService;

	public ConversionServiceHolder(ConversionService conversionService) {
		ConversionServiceHolder.conversionService = conversionService;
	}

	public static ConversionService get() {
		return conversionService;
	}

	public static <T> T convert(Object source, Class<T> targetType) {
		return conversionService.convert(source, targetType);
	}

}
