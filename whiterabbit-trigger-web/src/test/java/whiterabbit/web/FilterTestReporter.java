package whiterabbit.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import whiterabbit.ReportContext;
import whiterabbit.Reporter;

public class FilterTestReporter implements Reporter 
{
	public final Collection<Map<String,String>> collectedConfigurations = new ArrayList<Map<String,String>>();
	public final Collection<ReportContext> collectedTimeouts = new ArrayList<ReportContext>();
	public final Collection<ReportContext> collectedCancellations = new ArrayList<ReportContext>();
	

	@Override
	public void configure(Map<String, String> parameters) {
		collectedConfigurations.add(parameters);
	}
	

	@Override
	public void reportTimeout(ReportContext context) {
		collectedTimeouts.add(context);
	}


	public void reset() {
		collectedConfigurations.clear();
		collectedTimeouts.clear();
		collectedCancellations.clear();
	}


	@Override
	public void reportCancellation(ReportContext context) {
		collectedCancellations.clear();
	}

}
