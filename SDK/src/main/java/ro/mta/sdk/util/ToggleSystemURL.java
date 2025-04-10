package ro.mta.sdk.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class ToggleSystemURL {
    private final URL fetchTogglesURL;
    private final URL clientRegisterURL;
    private final URL evaluateToggleURL;
    private final URL constraintsURL;
    private final URL clientEvaluationZKPURL;

    public ToggleSystemURL(URI toggleSystemAPI) {
        try {
            String toggleSystemAPIstr = toggleSystemAPI.toString();
            fetchTogglesURL = URI.create(toggleSystemAPIstr + "/client/features").normalize().toURL();
            clientRegisterURL = URI.create(toggleSystemAPIstr + "/client/register").normalize().toURL();
            evaluateToggleURL = URI.create(toggleSystemAPIstr + "/client/evaluate").normalize().toURL();
            constraintsURL= URI.create(toggleSystemAPIstr + "/client/constraints").normalize().toURL();
            clientEvaluationZKPURL = URI.create(toggleSystemAPIstr + "/client/evaluateZKP").normalize().toURL();

        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Unleash API is not a valid URL: " + toggleSystemAPI);
        }
    }
    public URL getFetchTogglesURL() {
        return fetchTogglesURL;
    }
    public URL getClientRegisterURL() {
        return clientRegisterURL;
    }
    public URL getEvaluateToggleURL() {
        return evaluateToggleURL;
    }
    public URL getConstraintsURL() { return constraintsURL; }
    public URL getEvaluateToggleZKPURL() { return clientEvaluationZKPURL; }


}
