package whiterabbit;

import java.util.Map;


public interface Reporter {
	void configure(Map<String,String> parameters);
	void reportTimeout(ReportContext context);
	void reportCancellation(ReportContext context);
}
