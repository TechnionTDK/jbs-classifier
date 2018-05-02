/**
 * Created by netan on 19/02/2018.
 */
public class MentionsTuple {

    private String pasuk;
    private String context;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private String label;

    public MentionsTuple(){
    }

    public String getPasuk() {
        return pasuk;
    }

    public void setPasuk(String pasuk) {
        this.pasuk = pasuk;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public MentionsTuple(String pasuk, String context, String label){
        this.pasuk=pasuk;
        this.context=context;
        this.label = label;
    }
}
