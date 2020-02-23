package Lib;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PRManager
{
    private Map<String, PR> pullRequestMap;

    public PRManager()
    {
        pullRequestMap = new HashMap<>();
    }

    public void AddPullRequest(PR pullRequestToAdd,String msg)
    {
        pullRequestMap.put(msg,pullRequestToAdd);
    }

    public void SolvePR(String id, PRStatus status)
    {
        PR pr = pullRequestMap.get(id);
        pr.setStatus(status);
    }

    public Map<String,PR> getPullRequestMap(){
        return Collections.unmodifiableMap(pullRequestMap);
    }

    public void RemovePullRequest(String key)
    {
        pullRequestMap.remove(key);
    }
}
