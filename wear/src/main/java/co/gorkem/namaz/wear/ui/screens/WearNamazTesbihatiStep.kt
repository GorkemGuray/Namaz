package co.gorkem.namaz.wear.ui.screens

data class WearNamazTesbihatiStep(
    val title: String,
    val textArabic: String,
    val textTurkish: String,
    val meaning: String,
    val type: WearStepType,
    val targetCount: Int = 0
)

enum class WearStepType {
    PRAYER,
    COUNTER,
    COMPLETED
}

object WearNamazTesbihatiSteps {
    val steps = listOf(
        WearNamazTesbihatiStep(
            title = "1. İstiğfar & Selam",
            textArabic = "اَللّٰهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            textTurkish = "Allâhümme entes-selâmü ve minkes-selâm, tebârakte yâ zel-celâli vel-ikrâm.",
            meaning = "Allah'ım! Sen kurtuluş merciisin, esenlik ve selamet Sendendir. Ey celal ve ikram sahibi Allah'ım! Sen ne yücesin.",
            type = WearStepType.PRAYER
        ),
        WearNamazTesbihatiStep(
            title = "2. Salavat",
            textArabic = "عَلٰى رَسُولِنَا صَلَوَاتٌ\n\nاَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلٰى آلِ سَيِّدِنَا مُحَمَّدٍ",
            textTurkish = "Alâ Rasûlinâ salavât\n\nAllâhümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed.",
            meaning = "Peygamberimiz üzerine salavat getirin: Allah'ım! Efendimiz Muhammed'e ve onun âline (ailesine) salât ve selâm olsun.",
            type = WearStepType.PRAYER
        ),
        WearNamazTesbihatiStep(
            title = "3. Tevhid & Tesbih",
            textArabic = "سُبْحَانَ اللهِ وَالْحَمْدُ للهِ وَلَا إِلٰهَ إِلَّا اللهُ وَاللهُ أَكْبَرُ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ الْعَلِيِّ الْعَظِيمِ",
            textTurkish = "Subhânallâhi vel-hamdü lillâhi ve lâ ilâhe illallâhü vallâhü ekber. Ve lâ havle ve lâ kuvvete illâ billâhil-aliyyil-azîm.",
            meaning = "Allah'ı bütün noksan sıfatlardan tenzih eder, kemal sıfatlarla muttasıf olduğunu kabul ederim. Bütün hamd ve şükürler Allah'adır.",
            type = WearStepType.PRAYER
        ),
        WearNamazTesbihatiStep(
            title = "4. Ayet-el Kürsi",
            textArabic = "اَللّٰهُ لَا إِلٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْأَرْضِ مَنْ ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ وَلَا يُحِيطُونَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْأَرْضَ وَلَا يَؤُدُهُ حِفْظُهُمَا وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            textTurkish = "Allâhü lâ ilâhe illâ hüvel-hayyül-kayyûm. Lâ te'huzühû sinetün ve lâ nevm. Lehû mâ fis-semâvâti vemâ fil-ard. Menzellezî yeşfe'u 'indehû illâ bi-iznih. Ya'lemü mâ beyne eydîhim vemâ halfehüm. Velâ yuhîtûne bi-şey'in min 'ilmihî illâ bimâ şâ'. Vesi'a kürsiyyühüs-semâvâti vel-ard. Velâ ye'ûdühû hıfzuhümâ ve hüvel-aliyyül-azîm.",
            meaning = "Allah, O'ndan başka ilah yoktur; O, hayydir, kayyumdur. Kendisine ne uyku gelir ne de uyuklama.",
            type = WearStepType.PRAYER
        ),
        WearNamazTesbihatiStep(
            title = "5. Sübhanallah",
            textArabic = "وَهُوَ الْعَلِيُّ الْعَظِيمُ ذُو الْجَلَالِ سُبْحَانَ اللهِ",
            textTurkish = "Ve hüvel-aliyyül-azîmü zül-celâli sübhânallâh.",
            meaning = "O yücedir, büyüktür, celal sahibidir; Allah'ı noksan sıfatlardan tenzih ederim.",
            type = WearStepType.COUNTER,
            targetCount = 33
        ),
        WearNamazTesbihatiStep(
            title = "6. Elhamdülillah",
            textArabic = "سُبْحَانَ الْبَاقِي دَائِمًا الْحَمْدُ للهِ",
            textTurkish = "Sübhânel-bâgî dâimenil-hamdülillâh.",
            meaning = "Baki ve daimi olan Allah'a hamdolsun.",
            type = WearStepType.COUNTER,
            targetCount = 33
        ),
        WearNamazTesbihatiStep(
            title = "7. Allahuekber",
            textArabic = "رَبِّ الْعَالَمِينَ تَعَالَى شَانُهُ اَللهُ أَكْبَرُ",
            textTurkish = "Rabbil-âlemîne teâlâ şânühü Allâhü ekber.",
            meaning = "Alemlerin rabbi olan Allah'ın şanı yücedir, O en büyüktür.",
            type = WearStepType.COUNTER,
            targetCount = 33
        ),
        WearNamazTesbihatiStep(
            title = "8. Tahlil & Haşir",
            textArabic = "لَا إِلٰهَ إِلَّا اللّٰهُ وَحْدَهُ لَا شَرِيكَ لَهُ لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ\n\nاَللّٰهُمَّ احْشُرْنَا فِي زُمْرَةِ الصَّالِحِينَ",
            textTurkish = "Lâ ilâhe illallâhü vahdehû lâ şerîke leh. Lehül-mülkü ve lehül-hamdü ve hüve alâ külli şey'in kadîr.\n\nAllâhümmehşurnâ fî zümretis-sâlihîn.",
            meaning = "Allah Teala'dan başka ilah yoktur, tek ilah sadece odur, ortağı da yoktur. Bütün mülk O'na aittir.",
            type = WearStepType.PRAYER
        ),
        WearNamazTesbihatiStep(
            title = "9. Dua Vakti",
            textArabic = "",
            textTurkish = "Dua vakti. Ellerinizi açarak dilediğiniz gibi dua edebilirsiniz.",
            meaning = "Namazlardan sonra yapılan dualar kabul edilmeye daha yakındır.",
            type = WearStepType.PRAYER
        ),
        WearNamazTesbihatiStep(
            title = "10. Fatiha Suresi",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيمِ ﴿١﴾ اَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَمِينَ ﴿٢﴾ اَلرَّحْمٰنِ الرَّحِيمِ ﴿٣﴾ مَالِكِ يَوْمِ الدِّينِ ﴿٤﴾ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ﴿٥﴾ اِهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ ﴿٦﴾ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِم| غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّآلِّينَ ﴿٧﴾",
            textTurkish = "Bismillâhirrahmânirrahîm. Elhamdü lillâhi rabbil'alemin. Errahmânir'rahim. Mâliki yevmiddin. İyyâke na'büdü ve iyyâke neste'în. İhdinessırâtal müstakîm. Sırâtallezîne en'amte aleyhim gayrilmagdûbi aleyhim veleddâllîn. Âmin.",
            meaning = "Rahman ve Rahim olan Allah'ın adıyla. Hamd, Alemlerin Rabbi Allah'a mahsustur.",
            type = WearStepType.PRAYER
        ),
        WearNamazTesbihatiStep(
            title = "11. Tamamlandı",
            textArabic = "",
            textTurkish = "Kabûl Olsun",
            meaning = "Namaz tesbihatınız tamamlanmıştır. Allah kabul etsin!",
            type = WearStepType.COMPLETED
        )
    )
}
