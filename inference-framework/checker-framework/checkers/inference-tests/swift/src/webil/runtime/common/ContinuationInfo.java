package webil.runtime.common;

public class ContinuationInfo {
    public int placement;
    public int type;
    public boolean isHighIntegrity;
    public boolean isAutoEndorsed;
    
    public ContinuationInfo(int placement, int type, boolean isHighIntegrity, boolean isAutoEndorsed) {
        this.placement = placement;
        this.type = type;
        this.isHighIntegrity = isHighIntegrity;
        this.isAutoEndorsed = isAutoEndorsed;
    }
    
    public ClosureResult dispatch(WilRuntime runtime, Closure closure) throws Throwable {
        return null;
    }
    
    public FrameIndices getFrameIndices() {
        return null;
    }
}
