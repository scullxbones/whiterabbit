package whiterabbit.web;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import whiterabbit.Delay;
import whiterabbit.Rabbit;

public interface RabbitConfigurationSupplier {

	Rabbit.Builder configureRabbit(Rabbit.Builder builder);
	
	Delay configureDefaultTimeout();
	
	Map<Pattern,Delay> configureDelayMap();
	
	Set<Pattern> configureIgnoredUris();
}
