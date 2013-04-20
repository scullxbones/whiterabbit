package whiterabbit;

import java.util.Map;

public interface Cancelable {
	
	public interface Builder {
		Builder timeout(Delay delay);
		Builder named(String name);
		Builder extraInfo(Map<String,Object> context);
		Cancelable build();
	}
	
	void cancel();
}