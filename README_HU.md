# CentTerminal


Állásidő Terminál Program
Műszaki Leírás

 
Tartalomjegyzék

1.	Az állásidő terminál program.	2
2.	A program használata.	2
2.1	A berendezés kezelése.	4
3.	A program működése	7
3.1	rendszer követelmények	7
3.2	A rendszer felépítése	7
3.3	A program indulása	8
3.4	A program működése	9
3.4.1	Berendezés termel/ nem termel jel	9
3.4.2	Időszinkronizálás	10
3.4.3	PLC kommunikációs hiba	10
3.4.4	Kommunikáció a szerverrel	11
3.4.5	Parancssori kapcsolók	11
3.4.6	Események	11
3.5	Esemény napló	12
4.	Üzembe helyezett állásidő terminál programok	13
4.1	1700-as Dresszírozó	13
4.2	Georg Hasító	14
4.3	Húzvaegyengető	15

 

Az állásidő terminál program.

A állásidő terminál program a régi omronos ns és nt terminálok kiváltására szolgálnak. A régi rendszer kiváltása azért vált szükségessé, mert a terminálok gyakran tönkremennek, és a cseréje, illetve a javítása költséges.  A program kiváltja a plc-t is. A programnak köszönhetően az állásidő rendszer rugalmasabbá vált, könnyebben lehet egy új berendezést beilleszteni a rendszerbe. 

A program használata.

A program automatikusan elindul a számítógéppel együtt. A program elindulása után a következő képernyő fogad minket:


 


A program a felső sorban az állásidő regisztrálóhoz tartozó berendezés nevét láthatjuk, és a program verzió számát. A képen látható, hogy a dresszírozó terminálról készült. 
A felső szövegmezőben az ”A sor termel” szöveg szerepel, ilyenkor a berendezés üzemel. A szövegmezőben a következő üzenetek jelenhetnek meg: 

	A sor termel
	A sor áll
	Üzemszünet
	TMK
	Hiba a PLC kommunikációban

A következő sorban 3 mező található: 

	Leállás kezdete: A leállás kezdő időpontját mutatja.
	Leállás vége: A szerver aktuális időpontját mutatja. (Ha most elindulnának, akkor eddig az időpontig tartana az állás).
	Kód. Az állásidő terminálban megadott kód. Ha még nincs kód megadva, akkor 0000.  Ha a kód mező felé visszük az egérkurzort, akkor felugró ablakba kiírja a kód megnevezését. 

A következő nagyobb méretű fehér négyzetben a választható állásidő kódokat lehet látni. A fenti ábrán a kódok inaktívak, mert a sor termel, ilyenkor nem lehet állásidő kódot megadni. 

Az alatta lévő három nyomógombbal az állásidő kódot lehet megadni, megváltoztatni, vagy az állásidőt megosztani. 
A legalsó három nyomógombbal az állásidő három üzemmódját lehet használni. 

	Termelés
	Üzemszünet
	TMK

Az ablak legalján a kommunikáció állapotát lehet látni. Ha valamelyik kis négyzet piros a program nem tud működni. Ilyenkor jelezni kell a villanyszerelőknek. 









A berendezés kezelése.

Ha a berendezés megáll akkor a következő képernyőt láthatjuk:

 

Amikor a berendezés megáll, akkor a terminál a sor áll üzenetet írja ki.  A bal oldali dátum a leállás kezdetét, a jobb oldali dátum az aktuális időpontot mutatja.  A 0 –ás kód azt jelenti, hogy még nem választottak ki kódot.  Ilyenkor a kódokat tartalmazó mező aktívvá válik. 

 
Nincs más dolgunk, mint a fa szerkezetű állásidő kódok közül kiválasztjuk az aktuálisat.  Ha olyan állásidő kódot választottunk ki amit már nem lehet tovább bontani, akkor az állásidő megadása gomb aktív lesz. Ilyenkor, ha az állásidő megadása nyomógombot megnyomjuk, az aktuálisan megadott kód lesz a kiválasztott állásidő kód:

 

Az állásidő megadása nyomógomb megnyomása után a nyomógomb inaktív lesz. És a kiválasztott kód megjelenik a kód mezőben.  Ha az állásidő kódot nem választunk ki 2 percig, akkor a számítógépbe épített csipogó, és a számítógépre kötött hangfal beep –elni fog, egészen addig, míg a berendezés nem indul el, vagy nem választanak ki kódot.  

 

Ha a berendezés még továbbra is áll, és az állásidő fából egy új kódot választunk ki, akkor az állásidő módosítása és az állásidő megosztása nyomógomb aktív lesz. Ha az állásidő módosítás gombot nyomjuk, meg akkor felülírjuk (módosítjuk) a korábban megadott állásidő kódot. Ha az állásidő megosztása nyomógombot nyomjuk meg, akkor az állásidőt megosztjuk. A korábban megadott állásidő kód az állásidő kezdetétől a nyomógomb megnyomásáig lesz érvényben. Az újonnan megadott kód az állásidő megosztása nyomógomb megnyomásától az állásidő végéig lesz érvényben. 

Ha valamilyen hiba történik a kommunikációval, akkor a következő képet látjuk.:

 


Jelen esetben a PLC kommunikációval van probléma.  Ez azt jelenti, hogy a programhoz nem jut el a termel/nem termel jel a berendezéstől.  Ez nem feltétlenül a PLC hibáját jelzi, mert nem biztos, hogy a PLC küldi közvetlenül ezt a jelet. 
Az adatbázis hiba, azt jelenti, hogy az állásidő szerveren futó adatbázissal nincs kapcsolat. Az adatbázis az időszinkronizáláshoz, és a program elindulásához szükséges.
Ha az állásidő szerverhez lép fel kommunikációs probléma, akkor az azt jelenti, hogy nem sikerül kapcsolódni az állásidő szerver programhoz.  
Előfordulhat, hogy egyszerre több kommunikációs probléma is fellép egyszerre, ilyenkor lehet gond a számítógéppel is és a hálózattal is. 
Ha a program a PLC-vel kommunikál, de az állásidő szerverrel nem akkor a program működése nem áll le, továbbra is rögzíti a rekordokat. Amikor a szerverrel a kommunikáció helyre áll a program elküldi a szervernek a kódokat, így nem vesznek el. Amikor vannak olyan állásidő kódok, amiket nem sikerült elküldeni a szervernek akkor, ha az egér kurzorát a felső szövegdobozra visszük, akkor felugró ablakban megjelennek az eddig el nem küldött kódok.  Ha a számítógépet, vagy a programot újraindítjuk, akkor az el nem küldött kódok elvesznek!
A program naplóz minden eseményt, és egy hónapig tárolja. 


A program működése

rendszer követelmények

A program működéséhez hálózattal rendelkező számítógép szükséges, bármely windows vagy linux operációs rendszerrel. A program működéséhez a java 1.7 vagy újabb futató környezet telepítése szükséges. A telepítő program a következő weboldalon érhető el: http://www.oracle.com/technetwork/java/javase/downloads/index.html  
A számítógépnek a Dunaferres belső Ethernet hálózathoz csatlakoznia kell. 

A rendszer felépítése

 

Az állásidő terminál TCP/IP és SQL kapcsolattal kommunikál az állásidő szerverrel.  A PLC-től, vagy valamilyen közbeiktatott számítógéptől UDP kapcsolaton kapja a sor trmrl vagy a sor nem termel jelet. 

A program indulása

Minden számítógépen a ugyanaz a terminál program fut.  Azt hogy ez a terminál program melyik berendezéshez tartozik, a számítógép tulajdonságaitól függ. Ezeket a program az induláskor ellenőrzi.
A program induláskor ellenőrzi a saját számítógépének az IP címét. Csatlakozik az állásidő szerver adatbázisához, és a nodes táblába megkeresi hogy az adott számítógéphez van-e hozzátartozó berendezés.

 




1. táblázat nodes tábla (Nem teljes)

Ha a számítógép IP címe megegyezik a terminal_ip_address mezőben található rekorddal, akkor a hozzá tartozó adatokat,  (id, node_name, terminal_ip_address, server_port, plc_ip_address, plc_port adatokat letárolja a program.  Ha nem sikerül az adatbázishoz kapcsolódni, akkor a "Kivétel történt az SQL adatbázis kezelése közben:" hibaüzenetet kapjuk. Ha a számítógép IP címe nincs regisztrálva, akkor a "Kivétel történt az adatok inicializálása közben:" hibaüzenetet kapunk. Az üzenet bezárása után a program működése leáll.
Ezután az állásidő terminál program elindítja az állásidő ablakot, és lekérdezi, a szervez adatbázisából a berendezéshez tartozó állásidő kódokat. Az állásidő kódokból elkészíti az állásidő kód fa struktúrát. 
 



 

A program ekkor már működőképes. 

A program működése

Az állásidő terminál program működése esemény vezérelt. Minden esemény például sor termel vagy nem termel jel változása, nyomógomb megnyomása hatására történik a kijelző állapotában változás.

 Berendezés termel/ nem termel jel

A berendezés termel, vagy nem termel jelet a program UDP üzenetben kapja a plc_ip_address táblában szereplő IP címről  és a plc_port –ban meghatározott porton várja az üzenetet. Az üzenet egy darab short típusú változót tartalmaz, aminek az értéke 1, ha termel a berendezés, és 0, ha áll a berendezés.  


Időszinkronizálás

Az állásidő terminál program a kezelői csalások (pl.: számítógép óra átállítás), és a naptári óra átállítási problémák miatt nem használja a rendszeridőt. A rendszeridőt a program az állásidő szerver adatbázisából kéri le.  Ahhoz, hogy ne kelljen folyamatosan az adatbázist lekérdezni ezért a program a System.nanotime() függvényt használja.  Ez az idő a java virtuális gép indítása óta eltelt időt jelzi ki nanoszekundumban.  A szerver órájából és a virtuális gép futásának idejéből megkapjuk azt a korrekciós számot, ami alapján az aktuális futási időből pontosan tudjuk az időt. Majd ezt a korrekciós számot, minden álláskód küldéskor korrigáljuk (szinkronizáljuk). 

A korrekciós érték számítása:

diffTime=currentTimeMillis-  (System.nanoTime())/1000000

ahol:
	diffTime: a korrekciós érték milliszekundumban
	currenttimemillis: Az adatbázis szerver ideje milliszekundumban
	System.nanotime() : A java virtuális gép futási ideje nanoszekundumban
 
Az aktuális idő lekérdezésének a számítása a System.nanotime() –ból:

currentTimeMillis=(System.nanoTime())/1000000+diffTime


Ezzel a számított idővel nem lehet az állásidő programot átverni a rendszer óra átállítgatásával. 

PLC kommunikációs hiba

A PLC-től jövő kommunikáció egy irányú. UDP telegrammon keresztül kapjuk a Berendezés termel/ nem termel jelet.  A kommunikációt egy másodpercenként lefutó program ellenőrzi. Amikor kapunk egy üzenetet, a program letárja az üzenet érkezésének az időpontját. Másodpercenként a program kiszámítja, hogy mennyi idő telt el az utolsó üzenet érkezése óta. Amennyibe az eltelt idő meghaladja az 5 másodpercet, a program észleli, hogy kommunikációs hiba lépett fel a PLC-vel. Ekkor a program az állásidő kódot -10003 –ra változtatja.






Kommunikáció a szerverrel

A szerverrel TCP/IP kommunikációt valósítottunk meg. A szerver a TCP szerver, az állásidő terminál program a TCP kliens. A szerver küld egy 'D' üzenetet. Erre az állásidő terminál program válaszol.  A válasz üzenet felépítése:

Tipus	Megnevezés	Leírás
integer	status 	Állapot jelző bit 1: Minden rendben -10003 Kommunikációs hiba a plc-vel
long	aDowntime	Leállás időpontja
integer	aCode	Leállás Kódja ha nem ütött kódot akkor 0
long	rDowntimeStart	Leállás kezdete unixtime. (Record)
long	rDowntimeEnd	Leállás vége unixtime.(Record)
integer	rCode	Leállás kódja.(Record)


Az üzenet 3 részből áll. Az első rész a status azt küldi el, hogy a program rendben van, vagy sem.  Az üzenet második részébe az aktuális állapot kód jelenik meg. Az aDowntime a leállás időpontját tartalmazza. Ha a berendezés termel, akkor a leállás időpontja 0 .  Az aCode a Leállás kódja. Ha nem ütött kódot, akkor 0, ha termelnek, akkor null értéket ad.  Ha elindul a berendezés, akkor a program elküldi az eddig el nem küldött állásidő kódokat is. rDowntimeStart, rDowntimeEnd, rCode. Ha nincs mit elküldi akkor 0 értékeket küldünk. 

Parancssori kapcsolók

A programban az alapértelmezett beállításokat parancssori kapcsolóval lehet felülbírálni. A következő parancssori kapcsolókat fogadja el a program:

	beep:false (Beepelés letiltása)
	beepTimeOut:<idő> (A beepelés kezdete másodpercben)
	centralografIpAddress:<ipcím> (A centralográf ip címe)
	calculatedTimeEnable:false (A számított rendszeridő letiltása)

Események

A program működése a kommunikáción érkező üzenetek (sor termel / nem termel) és a felhasználói beavatkozások hatására működik. A felhasználó állásidő kódot választhat, illetve nyomógombot nyomhat.  
Amikor a sor megáll, akkor a program azt várja, hogy a felhasználó megadja a leállás okát. Amikor a sor elindul, a program beállítja az állásidő vége időpontot, és elküldi a szervernek. 
Nyomógomb események: 
	Állásidő megadása: A leállás kódja adható meg vele. Ez a nyomógomb akkor működtethető, ha termelés van megadva és a berendezés megállt és még nem ütöttünk állásidő kódot, illetve kiválasztottuk az állásidő okát. 
	Állásidő módosítása: A korábban megadott állásidő kódot lehet megváltoztatni. Ez a nyomógomb akkor működtethető, ha termelés van megadva és a berendezés megállt és már ütöttünk állásidő kódot, illetve kiválasztottuk az állásidő okát, ami nem egyezik meg a korábban megadott állásidő kóddal. 
	Állásidő megosztása: A korábban megadott állásidőt lehet vele lezárni, és új állásidő kódot lehet megadni. Ez a nyomógomb akkor működtethető, ha termelés van megadva és a berendezés megállt és már ütöttünk állásidő kódot, illetve kiválasztottuk az állásidő okát, ami nem egyezik meg a korábban megadott állásidő kóddal. 
	Termelés nyomógomb: Ezzel a nyomógombbal lehet megadni, hogy a berendezés termelő állapotban van.
	Üzemszünet nyomógomb: Ezzel a nyomógombbal lehet megadni, hogy üzemszünet van.
	TMK nyomógomb: Ezzel a nyomógombbal lehet megadni, hogy a berendezésen tervszerű megelőző karbantartást végeznek.

Esemény napló

A program eseménynaplót vezet a program működtetéséről. Az eseményeket 30 napig őrzi meg a program.  Példa az eseménynaplóra:

 



Üzembe helyezett állásidő terminál programok

Az állásidő terminál programok működése megegyezik, de a környezeti változók, például hogy hogyan érkezik, meg a sor termel jel a programhoz, minden berendezésnél egyedi. Eddig három berendezésen lett beüzemelve a program, ebben a fejezetben részletezem. 

1700-as Dresszírozó

A dresszírozón lett beüzemelve elsőként a terminál program.  Itt nem NS terminál volt, hanem egy Omron Supervisor program.  Kiépítésre került az új DFIR program. A Siemens AP1-es PLC-be egy új Ethernet vezérlő kártya lett beépítve, amin keresztül a telegramokat küldjük a DFIR – PLC interface számítógéphez. 



  
Amikor a berendezés elindul akkor a tárolni kívánt adatokat elküldi a PLC az interface számítógépnek. A számítógép másodpercenként ellenőrzi, hogy kapott-e adatot a PLC-től. Ha igen akkor a sor termel, ha nem akkor nem termel. Az így előállított jelet küldi a tovább az interface számítógép az állásidő terminál programnak UDP kapcsolatban. 

  


Georg Hasító

Georg hasítón az NS terminál lett kiváltva az állásidő terminál programra. A Georg hasítón telepítve lett egy OMRON PLC a kikészítői minősítő rendszerhez.  Az Omron plc 0.4-es bemenetére lett rákötve, hogy a sor termel vagy nem. A jel a sori SIEMENS S5-ös PLC-ből érkezik. A sor termel jelet a PLC tovább küldi a kikészítői adatgyűjtő szerver programnak az életjel 1. szavában. 






A minősítő program az életjelben kapott sor termel szót UDP kapcsolaton keresztül tovább küldi az állásidő terminál programnak.

 


Húzvaegyengető

A Húzvaegyengetőn az NS terminál lett kiváltva az állásidő terminál programra. A Húzvaegyengetőn telepítve lett egy OMRON PLC a kikészítői minősítő rendszerhez.  Az Omron plc 0.1-es bemenetére lett rákötve, hogy a sor termel vagy nem. A sor termel jelzéshez a berendezés figyelembe veszi az impulzusadóról jövő lemez sebességét is. A jel a sori SIEMENS S5-ös PLC-ből érkezik. A sor termel jelet a PLC tovább küldi a kikészítői adatgyűjtő szerver programnak az életjel 1. szavában. 

 



A minősítő program az életjelben kapott sor termel szót UDP kapcsolaton keresztül tovább küldi az állásidő terminál programnak.

 


