/**
 * WebServiceEndPointServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package api;

public class WebServiceEndPointServiceLocator extends org.apache.axis.client.Service implements api.WebServiceEndPointService {

    public WebServiceEndPointServiceLocator(String WebServiceEndPointPort_address) {
    this.WebServiceEndPointPort_address=WebServiceEndPointPort_address;
    }


    public WebServiceEndPointServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WebServiceEndPointServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WebServiceEndPointPort
    private String WebServiceEndPointPort_address = "";

    public String getWebServiceEndPointPortAddress() {
        return WebServiceEndPointPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String WebServiceEndPointPortWSDDServiceName = "WebServiceEndPointPort";

    public String getWebServiceEndPointPortWSDDServiceName() {
        return WebServiceEndPointPortWSDDServiceName;
    }

    public void setWebServiceEndPointPortWSDDServiceName(String name) {
        WebServiceEndPointPortWSDDServiceName = name;
    }

    public WebServiceEndPoint getWebServiceEndPointPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WebServiceEndPointPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWebServiceEndPointPort(endpoint);
    }

    public WebServiceEndPoint getWebServiceEndPointPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            WebServiceEndPointPortBindingStub _stub = new WebServiceEndPointPortBindingStub(portAddress, this);
            _stub.setPortName(getWebServiceEndPointPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWebServiceEndPointPortEndpointAddress(String address) {
        WebServiceEndPointPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (WebServiceEndPoint.class.isAssignableFrom(serviceEndpointInterface)) {
                WebServiceEndPointPortBindingStub _stub = new WebServiceEndPointPortBindingStub(new java.net.URL(WebServiceEndPointPort_address), this);
                _stub.setPortName(getWebServiceEndPointPortWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("WebServiceEndPointPort".equals(inputPortName)) {
            return getWebServiceEndPointPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://api.simula/", "WebServiceEndPointService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://api.simula/", "WebServiceEndPointPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("WebServiceEndPointPort".equals(portName)) {
            setWebServiceEndPointPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
