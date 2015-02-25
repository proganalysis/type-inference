import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class WilRuntimeClient implements AsyncCallback<Object> {
	
	public void onSuccess(Object result) {}

	public class ClientPrincipalCallback implements AsyncCallback<Object> {

		public void onSuccess(Object result) {}
		public void onFailure(Throwable caught) {}
	}
}