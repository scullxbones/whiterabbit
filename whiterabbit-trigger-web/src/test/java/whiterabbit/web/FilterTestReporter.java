package whiterabbit.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import whiterabbit.ReportContext;
import whiterabbit.Reporter;

public class FilterTestReporter implements Reporter 
{
	public static Collection<Map<String,String>> collectedConfigurations = new ArrayList<Map<String,String>>();
	public static Collection<ReportContext> collectedContexts = new ArrayList<ReportContext>();
	

	@Override
	public void configure(Map<String, String> parameters) {
		collectedConfigurations.add(parameters);
	}
	

	@Override
	public void reportTimeout(ReportContext context) {
		collectedContexts.add(context);
	}


	public static void reset() {
		collectedConfigurations.clear();
		collectedContexts.clear();
	}

}
