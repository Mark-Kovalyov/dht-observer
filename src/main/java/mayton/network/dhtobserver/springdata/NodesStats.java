package mayton.network.dhtobserver.springdata;

@Table
public class NodesStats {

    @PrimaryKey
    private String nodeId;

    private long findNodesRequests;
    private long getPeeersRequests;
    private long pingsRequests;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public long getFindNodesRequests() {
        return findNodesRequests;
    }

    public void setFindNodesRequests(long findNodesRequests) {
        this.findNodesRequests = findNodesRequests;
    }

    public long getGetPeeersRequests() {
        return getPeeersRequests;
    }

    public void setGetPeeersRequests(long getPeeersRequests) {
        this.getPeeersRequests = getPeeersRequests;
    }

    public long getPingsRequests() {
        return pingsRequests;
    }

    public void setPingsRequests(long pingsRequests) {
        this.pingsRequests = pingsRequests;
    }
}
