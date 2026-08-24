package lu.kbra.pclib.db.utils;

import java.util.Map;

import lu.kbra.pclib.db.impl.HintsOwner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DelegatingHintOwner implements HintsOwner {

	final Map<String, Object> hints;

}
