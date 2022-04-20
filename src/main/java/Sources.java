public enum Sources {
    ASFU(Sources.class.getClassLoader().getResource("xls_samples/Rasporyazhenie_na_provedenie_platezhei_ASFU.xls").getPath()),
    KAZAN_SBIT(Sources.class.getClassLoader().getResource("xls_samples/Rasporyazhenie_na_provedenie_platezhei_KaznaSbyt.xlsx").getPath()),
    BIKTTS(Sources.class.getClassLoader().getResource("xls_samples/Reestr_na_oplatu_BiK_TTS.xlsx").getPath()),
    UPP(Sources.class.getClassLoader().getResource("xls_samples/Reestr_platezhei_FEU.xlsx").getPath()),
    FEU(Sources.class.getClassLoader().getResource("xls_samples/Reestr_platezhei_UPP.xlsx").getPath());

    private final String path;

    Sources(String path) {
        if (path.charAt(0) == '/') {
            this.path = path.substring(1);
        } else {
            this.path = path;
        }
    }

    public String
    getPath() {
        return path;
    }
}
