package com.simula.esocl.test.api;

public class WebServiceEndPointProxy implements WebServiceEndPoint {
    private String _endpoint = null;
    private WebServiceEndPoint webServiceEndPoint = null;

    public WebServiceEndPointProxy() {
        _initWebServiceEndPointProxy();
    }

    public WebServiceEndPointProxy(String endpoint) {
        _endpoint = endpoint;
        _initWebServiceEndPointProxy();
    }

    private void _initWebServiceEndPointProxy() {
        try {
            webServiceEndPoint = (new WebServiceEndPointServiceLocator("")).getWebServiceEndPointPort();
            if (webServiceEndPoint != null) {
                if (_endpoint != null)
                    ((javax.xml.rpc.Stub) webServiceEndPoint)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
                else
                    _endpoint = (String) ((javax.xml.rpc.Stub) webServiceEndPoint)._getProperty("javax.xml.rpc.service.endpoint.address");
            }

        } catch (javax.xml.rpc.ServiceException serviceException) {
        }
    }

    public String getEndpoint() {
        return _endpoint;
    }

    public void setEndpoint(String endpoint) {
        _endpoint = endpoint;
        if (webServiceEndPoint != null)
            ((javax.xml.rpc.Stub) webServiceEndPoint)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

    }

    public WebServiceEndPoint getWebServiceEndPoint() {
        if (webServiceEndPoint == null)
            _initWebServiceEndPointProxy();
        return webServiceEndPoint;
    }

    public boolean getResult(String arg0, byte[] arg1) throws java.rmi.RemoteException {
        if (webServiceEndPoint == null)
            _initWebServiceEndPointProxy();
        return webServiceEndPoint.getResult(arg0, arg1);
    }


}