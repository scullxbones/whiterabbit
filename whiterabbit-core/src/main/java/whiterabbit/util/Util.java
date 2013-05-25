package whiterabbit.util;

public class Util {

	private Util() {}
	
	@SafeVarargs
	public static final <T> T firstNonNull(T... objects) {
		for(T each: objects) {
			if (each != null)
				return each;
		}
		throw new IllegalArgumentException("There are no non-null objects");
	}
	
	public static final <T> T valueOrDefault(T value, T defaultValue) {
		return firstNonNull(value,defaultValue);
	}
	
}
