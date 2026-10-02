package co.gorkem.namaz.ui.screens.zikirmatik

data class NamazTesbihatiStep(
    val title: String,
    val textArabic: String,
    val textTurkish: String,
    val meaning: String,
    val type: StepType,
    val targetCount: Int = 0,
    val handGesture: String? = null
)

enum class StepType {
    PRAYER,     // Text-based prayer step
    COUNTER,    // Counter step
    COMPLETED   // End screen
}

enum class TesbihatMode(val displayName: String) {
    KISA("Kısa"),
    UZUN("Uzun")
}

enum class TesbihatPrayerTime(val displayName: String) {
    SABAH("Sabah"),
    OGLE("Öğle"),
    IKINDI("İkindi"),
    AKSAM("Akşam"),
    YATSI("Yatsı")
}

object NamazTesbihatiSteps {
    val steps = listOf(
        NamazTesbihatiStep(
            title = "1. İstiğfar ve Selam",
            textArabic = "اَللّٰهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            textTurkish = "Allâhümme ente’s-selâmü ve minke’s-selâm, tebârekte yâ ẕe’l-celâli ve’l-ikrâm.",
            meaning = "Allah'ım! Sen kurtuluş merciisin, esenlik ve selamet Sendendir. Ey celal ve ikram sahibi Allah'ım! Sen ne yücesin.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "2. Salavat",
            textArabic = "عَلٰى رَسُولِنَا صَلَوَاتٌ\n\nاَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلٰى آلِ سَيِّدِنَا مُحَمَّدٍ",
            textTurkish = "Alâ Rasûlinâ salavât\n\nAllâhümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed.",
            meaning = "Peygamberimiz üzerine salavat getirin: Allah'ım! Efendimiz Muhammed'e ve onun âline (ailesine) salât ve selâm olsun.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "3. Tevhid ve Tesbih Duası",
            textArabic = "سُبْحَانَ اللهِ وَالْحَمْدُ للهِ وَلَا إِلٰهَ إِلَّا اللهُ وَاللهُ أَكْبَرُ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ الْعَلِيِّ الْعَظِيمِ",
            textTurkish = "Sübḥânallahi ve’l-ḥamdü lillâhi ve lâ ilâhe illâllahü vallahü ekber ve lâ ḥavle ve lâ ḳuvvete illâ billâhi’l-ʿaliyyi’l-ʿaẓîm.",
            meaning = "Allah'ı bütün noksan sıfatlardan tenzih eder, kemal sıfatlarla muttasıf olduğunu kabul ederim. Bütün hamd ve şükürler Allah'adır. Allah'tan başka hiçbir ilah yoktur. İhtiyaçları gideren ve zararları yok eden yalnız yüce ve güçlü olan Allah'tır.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "4. Âyet-el Kürsî",
            textArabic = "اَللّٰهُ لَا إِلٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْأَرْضِ مَنْ ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ وَلَا يُحِيطُونَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْأَرْضَ وَلَا يَؤُدُهُ حِفْظُهُمَا وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            textTurkish = "Bismillâhirraḥmânirraḥîm. Allahü lâ ilâhe illâ hüve’l-ḥayyü’l-ḳayyûm. Lâ te’ḫuẕühû sinetün ve lâ nevm. Lehû mâ fi’s-semâvâti vemâ fi’l-arḍı. Menẕelleẕî yeşfeʿu ʿındehû illâ bi-iẕnih. Yâʿlemü mâ beyne eydîhim ve mâ ḫalfehüm ve lâ yuḥîṭûne bi-şey’in min-ʿılmihî illâ bi-mâ şâe. Vesiʿa kürsiyyühü’s-semâvâti ve’l-arḍi. Velâ yeûdühû ḥıfẓuhümâ ve hüve’l-ʿaliyyü’l-ʿaẓîm.",
            meaning = "Allah, O'ndan başka ilah yoktur; O, hayydir, kayyumdur. Kendisine ne uyku gelir ne de uyuklama. Göklerde ve yerdekilerin hepsi O'nundur. İzni olmadan O'nun katında kim şefaat edebilir? O, kullarının yaptıklarını ve yapacaklarını bilir. O'nun bildirdiklerinin dışında insanlar O'nun ilminden hiçbir şeyi tam olarak bilemezler. O'nun kürsüsü gökleri ve yeri içine alır, onları koruyup gözetmek kendisine zor gelmez. O, yücedir, büyüktür.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "5. Sübhanallah",
            textArabic = "وَهُوَ الْعَلِيُّ الْعَظِيمُ ذُو الْجَلَالِ سُبْحَانَ اللهِ",
            textTurkish = "Ve hüve’l-ʿaliyyü’l-ʿazîmu zü’l-celâli sübḥânellâh.",
            meaning = "O yücedir, büyüktür, celal sahibidir; Allah'ı noksan sıfatlardan tenzih ederim.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "6. Elhamdülillah",
            textArabic = "سُبْحَانَ الْبَاقِي دَائِمًا الْحَمْدُ للهِ",
            textTurkish = "Sübḥâne’l-bâḳî dâimeni’l-ḥamdülillâh.",
            meaning = "Baki ve daimi olan Allah'a hamdolsun.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "7. Allahuekber",
            textArabic = "رَبِّ الْعَالَمِينَ تَعَالَى شَانُهُ اَللهُ أَكْبَرُ",
            textTurkish = "Rabbi’l-ʿâlemîne teʿâlâ şânühü Allâhü Ekber.",
            meaning = "Alemlerin rabbi olan Allah'ın şanı yücedir, O en büyüktür.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "8. Tahlil ve Haşir Duası",
            textArabic = "لَا إِلٰهَ إِلَّا اللّٰهُ وَحْدَهُ لَا شَرِيكَ لَهُ لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ\n\nاَللّٰهُمَّ احْشُرْنَا فِي زُمْرَةِ الصَّالِحِينَ",
            textTurkish = "Lâ ilâhe illallâhü vaḥdehû lâ şerîke leh. Lehü’l-mülkü ve lehü’l-ḥamdü ve hüve ʿalâ külli şey’in ḳadîr.\n\nAllâhümmehşurnâ fî zümretis-sâlihîn.",
            meaning = "Allah Teala'dan başka ilah yoktur, tek ilah sadece odur, ortağı da yoktur. Bütün mülk O'na aittir. Bütün hamdüsenalar O'nadır. O her şeye kadirdir. Allah'ım bizi salihler zümresiyle haşreyle.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "9. Dua Aşaması",
            textArabic = "",
            textTurkish = "Dua vakti. Ellerinizi açarak dilediğiniz gibi dua edebilirsiniz.",
            meaning = "Peygamber Efendimiz (SAV) namazlardan sonra yapılan duaların kabul edileceğini müjdelemiştir. İçtenlikle isteklerinizi Rabbimize iletin.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "10. Fâtiha Sûresi",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيمِ ﴿١﴾ اَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَمِينَ ﴿٢﴾ اَلرَّحْمٰنِ الرَّحِيمِ ﴿٣﴾ مَالِكِ يَوْمِ الدِّينِ ﴿٤﴾ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ﴿٥﴾ اِهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ ﴿٦﴾ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّآلِّينَ ﴿٧﴾",
            textTurkish = "Bismillâhirrahmânirrahîm. Elhamdü lillâhi rabbi’l-âlemîn. Er-rahmâni’r-rahîm. Mâliki yevmi’d-dîn. İyyâke na’büdü ve iyyâke neste’în. İhdina’s-sırâta’l-müstakîm. Sırâtallezîne en’amte aleyhim gayri’l-magdûbi aleyhim veleddâllîn. Âmin.",
            meaning = "Rahman ve Rahim olan Allah'ın adıyla. Hamd, Alemlerin Rabbi Allah'a mahsustur. O, Rahman'dır, Rahim'dir. Hesap ve ceza gününün malikidir. Yalnız sana ibadet eder ve yalnız senden yardım dileriz. Bizi doğru yola, kendilerine nimet verdiklerinin yoluna ilet; gazaba uğrayanlarınkine ve sapkınlarınkine değil.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "11. Tamamlandı",
            textArabic = "",
            textTurkish = "Kabûl Olsun",
            meaning = "Namaz tesbihatınız tamamlanmıştır. Allah dualarınızı ve tesbihatınızı kabul eylesin!",
            type = StepType.COMPLETED
        )
    )
}
