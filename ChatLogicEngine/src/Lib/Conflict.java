package Lib;

public class Conflict
{
    MergeConflicts mergeConflicts;
    String ConflictPath;
    String oursContent;
    String theirsContent;
    String ancestorContent;

    public Conflict(MergeConflicts mergeConflicts, String conflictPath, String oursContent, String theirsContent, String ancestorContent) {
        this.mergeConflicts = mergeConflicts;
        ConflictPath = conflictPath;
        this.oursContent = oursContent;
        this.theirsContent = theirsContent;
        this.ancestorContent = ancestorContent;
    }


}
