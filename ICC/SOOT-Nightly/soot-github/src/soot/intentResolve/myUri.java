package soot.intentResolve;

import java.net.URI;
import com.sleepycat.persist.model.Persistent;


@Persistent public	class myUri{
		public String scheme;
		public String host;
		public String port;
		public String path;
		public String pathPattern;
		public String pathPrefix;
		public myUri(){
			scheme = "";
			host = "";
			port = "";
			path = "";
			pathPattern = "";
			pathPrefix = "";
		}
		boolean isEmpty(){
			boolean result = (scheme.equals("") && host.equals("") && port.equals("") && 
							path.equals("") && pathPattern.equals("") && 
							pathPrefix.equals("") );
			return result;
		}

		public boolean match(String UriString){
			URI uri = URI.create(UriString);
			String uHost = uri.getHost();
			int nPort = uri.getPort();
			String uPort = Integer.toString(uri.getPort());
			if(uHost == null){
				return false;
			}
			if(uHost.compareToIgnoreCase(this.host) != 0){
				return false;
			}
			if(nPort>=0){
				if(! uPort.equals(this.port)){
					return false;
				}
			}

			return true;
		}
	}
