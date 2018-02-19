/**
 * Created by netan on 19/02/2018.
 */
public class MentionsTuple {

    private String pasuk;
    private String context;

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

    public MentionsTuple(String pasuk, String context){
        this.pasuk=pasuk;
        this.context=context;
    }
}
