package com.sietecerouno.atlantetransportador.assets;

import org.json.JSONObject;

/**
 * Created by MAURICIO on 21/07/17.
 */

public interface ListenerTpaga {

    void dataPhpReturn(JSONObject output);
    void dataPhpError(String output);

}
