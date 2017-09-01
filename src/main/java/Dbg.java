    

public enum Dbg {
        NONE(0x0),
	ERROR(0x1),
	INFO(0X2),
        PAGE(0x4),
	CAT(0x8),
        FOUND(0x10),
        FINAL(0x20),
	URI(0x40),
	ANY(0xffffffff);

        int id;

        Dbg(int id) {
                this.id = id;
        }
    

    public static int enabledFlags = PAGE.id | ERROR.id;

    public static void dbg(int flags, String s){
        if ((flags & enabledFlags) != 0)  System.out.println(s);
    }
}
