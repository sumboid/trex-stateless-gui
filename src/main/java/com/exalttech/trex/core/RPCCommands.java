package com.exalttech.trex.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exalttech.trex.remote.models.common.RPCResult;
import com.exalttech.trex.remote.models.stats.ActivePGIdsRPCResult;
import com.exalttech.trex.remote.models.stats.PGIdStatsRPCResult;


public class RPCCommands {
    private static final class Commands {
        private static final String GET_ACTIVE_PGIDS = "get_active_pgids";
        private static final String GET_PGID_STATS = "get_pgid_stats";
    }

    private RPCCommands() {}

    public static ActivePGIdsRPCResult getActivePGIds() throws IOException {
        final String jsonResult = sendRequest(Commands.GET_ACTIVE_PGIDS, null);
        return new ObjectMapper().readValue(jsonResult, ActivePGIdsRPCResult.class);
    }

    public static PGIdStatsRPCResult getPGIdStats(List<Integer> pgIds) throws IOException {
        if (pgIds == null) {
            pgIds = new ArrayList<>();
        }
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("pgids", pgIds);
        final String jsonResult = sendRequest(Commands.GET_PGID_STATS, parameters);
        return new ObjectMapper().readValue(jsonResult, PGIdStatsRPCResult.class);
    }

    // Should be removed from here, it is needed only for old architecture
    private static String sendRequest(final String command, final Map<String, Object> parameters) throws
            IOException
    {
        String stringParameters = "";
        if (parameters != null) {
            final String jsonParameters = new ObjectMapper().writeValueAsString(parameters);
            stringParameters = jsonParameters.substring(1, jsonParameters.length() - 1);
        }
        final String jsonRPCResult = ConnectionManager.getInstance().sendRequest(command, stringParameters);
        final RPCResult[] rpcResult = new ObjectMapper().readValue(jsonRPCResult, RPCResult[].class);
        return rpcResult[0].getResult();
    }
}
