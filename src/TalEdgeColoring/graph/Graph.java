package TalEdgeColoring.graph;

import TalEdgeColoring.MemoryHelper;

import java.util.*;

/**
 * Created by Malgorzata on 2017-04-18.
 */

public class Graph {
    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;
    private int colorNum; // liczba zuzytych kolorow
    private int degree;
    private ArrayList<ArrayList<Integer>> neighbourhoodMatrix; //macierz sasiedztwa

    public Graph() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        colorNum = 0;
        degree = 0;
        neighbourhoodMatrix = new ArrayList<ArrayList<Integer>>();
    }

    /**
     * Dodaje wierzcholki: 0, 1, 2, ..., vnumber - 1
     *
     * @param vnumber liczba wierzcholkow
     */
    public void addVertices(int vnumber) {
        for (int i = 0; i < vnumber; ++i)
            vertices.add(new Vertex(i));
    }

    /**
     * @param v1 jeden koniec krawedzi
     * @param v2 drugi koniec krawedzi
     * @param id id krawedzi
     */
    public void addEdge(int v1, int v2, int id) {
        if (v1 < vertices.size() && v2 < vertices.size() && v1 != v2) {
            Edge e = new Edge(vertices.get(v1), vertices.get(v2), id);
            edges.add(e);
            vertices.get(v1).addNeigbour(vertices.get(v2), e);
            vertices.get(v2).addNeigbour(vertices.get(v1), e);
        }
    }

    /**
     * @return stopien grafu
     */
    public int getDegree() {
        if (degree == 0) {
            for (Vertex v : vertices) {
                if (v.getDegree() > degree)
                    degree = v.getDegree();
            }
        }
        return degree;
    }

    public int getVerticesSize() {
        return this.vertices.size();
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<Vertex> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    public int getColorNum() {
        return colorNum;
    }

    public void setColorNum(int colorNum) {
        this.colorNum = colorNum;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    //pobieranie macierzy sasiedztwa
    public ArrayList<ArrayList<Integer>> getNeighbourhoodMatrix() {
        return this.neighbourhoodMatrix;
    }

    //<editor-fold desc="NTL">

    /**
     * Algorytm NTL
     *
     * @return ile nanosekund trwalo wykonywanie algorytmu
     */
    public ColoringResult colorNTL() {
        MemoryHelper memoryHelper = new MemoryHelper();
        memoryHelper.resetPeak();
        long lStartTime = System.nanoTime();
        int degree = getDegree(); //O(|V|)
        // O(|E|)
        if (degree <= 2) {
            colorNC();
        } else {
            int q = degree + 1;
            // O(|E|)
            for (Edge e : edges) { //|E| razy

                int kolor = e.setMinCol(q);//O(2*degree - 2 )
                if (kolor < 0) {
                    Recolor(e.getV1(), e.getV2()); // wywolanie procedury Recolor
                } else {
                    e.setColor(kolor);
                }

                // wypelnianie macierzy sasiedztwa faktycznymi wartosciami
                neighbourhoodMatrix.get(e.getV1Id()).set(e.getV2Id(), e.getColor());
                neighbourhoodMatrix.get(e.getV2Id()).set(e.getV1Id(), e.getColor());

                e.setActualMissingColor(degree);
                e.getV1().removeMissingColor(e.getColor());//uaktualniania missingColor
                e.getV2().removeMissingColor(e.getColor());//uaktualniania missingColor
                e.getV1().setActualMissingColor(degree);
                e.getV2().setActualMissingColor(degree);
            }

        }
        long lEndTime = System.nanoTime();
        long difference = lEndTime - lStartTime;
        long memory = memoryHelper.getPeakUsage();

        //sprawdzenie, jaki jest najwiekszy kolor
        colorNum = 0;
        for (Edge e : edges) {
            if (e.getColor() > colorNum)
                colorNum = e.getColor();
        }

        if (!test())
            return null;

        return new ColoringResult(colorNum, difference, memory);
    }

    public long colorNC() {
        long lStartTime = System.nanoTime();
        int degree = getDegree(); //O(|V|)
        Collections.shuffle(edges); // ustawienie krawedzi w losowej kolejnosci,
        // O(|E|)
        for (Edge e : edges) { //|E| razy

            int tmp = e.setMinCol(2 * degree - 1);
            if (colorNum > 0) {
                if (tmp < 0)  {//O(2*degree - 2 )
                    System.err.println("Przekroczono limit kolorow");
                    break;
                }

                else if (tmp >= colorNum )
                    ++ colorNum;
            }
            else
                e.setColor(++ colorNum); //M: zmieniłam, żeby na początku nadawany był kolor 1, a nie 0
        }
        long lEndTime = System.nanoTime();
        long difference = lEndTime - lStartTime;
        test();
        return difference;
    }

    boolean test() {
        for (Vertex v : vertices) {
            TreeSet<Integer> colors = new TreeSet<>();
            for (int i = 0; i < v.getAdjacencyList().size(); ++i) {
                int vid = v.getAdjacencyList().get(i).getId();
                if (v.getEdge(vid).getColor() > 0)
                    colors.add(v.getEdge(vid).getColor());

            }
            if (colors.size() != v.getAdjacencyList().size()) {
                System.out.println("ERROR " + v.getId() + " " + v.getAdjacencyList().size() + " " + colors.size());
			/*for ( int i = 0; i < v.getAdjacencyList().size(); ++ i) {
				System.out.println(v.getAdjacencyList().get(i).getId() + " " + v.getEdge(v.getAdjacencyList().get(i).getId()).getColor());
			}*/
                return false;
            }
        }
        return true;
    }

    /**
     * Procedura Recolor - przekolorowywanie krawedzi w Algorytmie NTL
     *
     * @return ile nanosekund trwalo wykonywanie procedury
     */
    public void Recolor(Vertex v1, Vertex v2) {

        //1. Tworzymy wachlarza dla krawedzi V1-V2
        ArrayList<Integer> FanV1 = new ArrayList<>();
        //2. Wachlarz rozpoczyna sie od wierzcholka v2
        FanV1.add(v2.getId());
        //3. Tworzymy HashMape w celu sprawdzenia czy dany wierzcholek nalezy juz do wachlarza (zeby nie duplikowac)
        HashMap<Integer, Boolean> FanV1Tmp = new HashMap<>(); //najpierw dodaje do TreeSetu, a potem przepisuje do listy
        //TreeSety nie dodaja duplikujacych sie elementow
        //4. Do HashMapy dodajemy pierwszy wierzcholek wachlarza V2
        FanV1Tmp.put(v2.getId(), true);
        //5. Definiujemy kolor alfa, jako kolor brakujacy wierzcholka V1
        int alfa = v1.getMissingColor();
        //6. szukamy wierzcholka ktory jest polaczony z V1 krawedzia o kolorze, ktory jest rowny kolorowi brakujacemu wierzcholka V2
        Vertex tmpVertex = v2;

        boolean ifEnd = false;
        int zliczanie = 0;

        while (!ifEnd && zliczanie < edges.size()) {

            ifEnd = true;
            //8. Przechodzimy po wszystkich sasiadach V1....
            for (Vertex v : v1.getAdjacencyList()) {

                //9. Jezeli sasiad V1 jest z nim polaczony kolorem brakujacym poprzedniego poprzedniego wierzchlka wachlarza...

                if (v1.getEdge(v.getId()).getColor() == tmpVertex.getMissingColor()) {
                    int i = v.getId();
                    //10. I jeżeli dany wierzcholek nie nalezy jeszcze do wachlarza...
                    if (FanV1Tmp.get(v.getId()) == null) {
                        //11. To zmieniamy tmpVertex...
                        tmpVertex = vertices.get(i);

                        //12. Oraz dodajemy do wachlarza oraz HashMapy ten wierzcholek
                        FanV1.add(i);
                        FanV1Tmp.put(i, true);
                        ifEnd = false;
                        break;
                    }
                }
            }
            //13. Poprzez zliczanie kontrolujemy czy nasz wachlarz nie sklada sie z wiekszej liczby krawedzi niz istnieje w grafie
            zliczanie++;
        }

        //Fan size nie moze byc rowne 1, bo skoro nie mozna
        //dac wspolnego koloru v1 i v2, to jakas krawedz incydenta do v1 ma
        //kolor brakujacy v2
        //14. Wachlarz zostal skonstruowany. TmpVertex oznacza teraz ostatni wierzcholek wachlarza -Xs
        //15. Definiujemy kolor beta jako kolor brakujacy ostatniego wierzcholka wachlarz - Xs
        int beta = tmpVertex.getMissingColor();

        //16. Jezeli kolor brakujacy Xs jest rowniez kolorem brakujacym V1, to mozemy przesunac wachlarz, a V1-XS pokolorowac kolorem beta
        if (v1.missingColor(beta)) {
            //17. Przesuwamy wachlarz: teraz krawedz v1-v2 jest pokolorowane, a krawedz v1-xs(tmpVertex) nie
            for (int i = 0; i < FanV1.size() - 1; i++) {
                //18. W macierzach sasiedztwa zmieniamy kolory krawedzi (przesuniecie wachlarza)
                neighbourhoodMatrix.get(v1.getId()).set(FanV1.get(i), neighbourhoodMatrix.get(v1.getId()).get(FanV1.get(i + 1)));
                neighbourhoodMatrix.get(FanV1.get(i)).set(v1.getId(), neighbourhoodMatrix.get(v1.getId()).get(FanV1.get(i + 1)));

                //19. Dodajemy wierzcholkom wachlarz nowy kolor brakujacy (ten, ktory jeszcze chwilowo, przed przesunieciem, okresla krawedz, ktora przesuwamy)
                vertices.get(FanV1.get(i)).addMissingColor(v1.getEdge(FanV1.get(i)).getColor());
                //20. Zmieniamy kolor krawdzi od V1 do aktualnego wierzcholka wachlarza, na kolor nastepnej krawedzi
                v1.getEdge(FanV1.get(i)).setColor(neighbourhoodMatrix.get(v1.getId()).get(FanV1.get(i + 1)));
                //21. Usuwamy niepotrzebny kolor brakujacy wierzcholka wachlarza, ktory przesunelismy
                vertices.get(FanV1.get(i)).removeMissingColor(v1.getEdge(FanV1.get(i)).getColor());
            }
            //22. ...Ustalamy kolor krawedzi miedzy v1 a xs(tmpVertex) na 0 (czyli usuwamy ta krawedz)
            v1.getEdge(tmpVertex.getId()).setColor(0);
            //23. Na podstawie macierzy sasiedztwa (jeszcze nie aktualnej) dodajemy odpowiedni kolor brakujacy do xs (tmpVertex) - spowodowane jest to przesunieciem wachlarza
            tmpVertex.addMissingColor(neighbourhoodMatrix.get(v1.getId()).get(tmpVertex.getId()));
            //24. Dla v1 i wszystkich wiercholkow nalezacych do wachlarza aktualizujemy kolory brakujace
            v1.setActualMissingColor(degree);
            for (int i = 0; i < FanV1.size(); i++) {
                vertices.get(FanV1.get(i)).setActualMissingColor(degree);
                v1.getEdge(FanV1.get(i)).setActualMissingColor(degree);
            }
            // 25. Poniewaz beta jest kolorem brakujacym V1 i XS, mozemy pokolorowac krawedz x1-xs kolorem beta
            // 26. Aktualizujemy macierz sasiedztwa
            neighbourhoodMatrix.get(v1.getId()).set(tmpVertex.getId(), beta);
            neighbourhoodMatrix.get(tmpVertex.getId()).set(v1.getId(), beta);
            v1.getEdge(tmpVertex.getId()).setColor(beta);
            // 27. Dodajemy beta do kolorow brakujacych V1 i XS (tmpVertex)
            v1.removeMissingColor(beta);//!!
            tmpVertex.removeMissingColor(beta);
            v1.setActualMissingColor(degree);
            tmpVertex.setActualMissingColor(degree);
        }
        // 28. Jezeli beta (kolor brakujacy XS (tmpVertex) nie jest kolorem brakujacym V1,
        else {
            // 29. Tworzymy sciezke o poczatku w XS i skladajaca sie z krawedzi na przemian kolorow ALFA i BETA
            ArrayList<Integer> Path = new ArrayList<Integer>();
            // 30. Dodajemy pierwszy wezel sciazki XS (tmpVertex)
            Path.add(tmpVertex.getId());
            // 31. beta jest kolorem brakujacym XS, a zatem sciezka nie moze zaczynac sie od BETA. Zatem sciezka, jezeli istnieje, zacyzna sie od ALFA
            // 32. Jako tmptmpVertex oznaczamy wierzcholek, nalezacy do sciezki, ktory aktualnie przetwarzamy (na start: XS czyli tmpVertex)
            Vertex tmptmpVertex = tmpVertex;
            // 33. Zmienna licznik pomaga nam kontrolowac, czy nie przegladamy wiecej sciezek niz jest w grafie
            int licznik = 0;
            boolean koniec = false;
            // 34. Zmienna parzystosc, pozwala kontrolowac czy nastepna krawedz powinna byc pokolorowana kolorem ALFA czy BETA
            int parzystosc = 0;
            // 35. W petli szukamy kolejnych krawedzi sciezki Path. W zaleznosci szukamy krawedzi koloru ALFA lub BETA
            while (licznik < edges.size() && !koniec) {
                koniec = true;
                // 36. Jezeli jestesmy w kroku parzystym (zaczynamy od 0) szukamy krawedzi koloru ALFA (pierwszy krok musi byc koloru ALFA, bo BETA jest kolorem brakujacym XS)
                if (parzystosc % 2 == 0) {
                    // 37. Dla wszystkich wierzcholkow sprawdzamy czy....
                    for (int i = 0; i < vertices.size(); i++) {
                        // 38. ...sa poloczone z koncem dotychczasowej sciezki kolorem alfa.
                        if (neighbourhoodMatrix.get(tmptmpVertex.getId()).get(i) == alfa && Path.indexOf(i) < 0) {
                            // 39. Jezeli tak, dodajemy kolejny wierzcholek do sciezki i zmieniamy tmptmpVertex na dodany wierzcholek
                            tmptmpVertex = vertices.get(i);
                            Path.add(i);
                            koniec = false;
                            break;
                        }
                    }
                }
                // 40. Jezeli jestesmy w kroku nieparzystym, szukamy krawedzi koloru BETA
                else {
                    // 41. Dla wszystkich wierzcholkow sprawdzamy czy....
                    for (int i = 0; i < vertices.size(); i++) {
                        // 42. ...sa poloczone z koncem dotychczasowej sciezki kolorem beta.
                        if (neighbourhoodMatrix.get(tmptmpVertex.getId()).get(i) == beta && Path.indexOf(i) < 0) {
                            // 43. Jezeli tak, dodajemy kolejny wierzcholek do sciezki i zmieniamy tmptmpVertex na dodany wierzcholek
                            tmptmpVertex = vertices.get(i);
                            Path.add(i);
                            koniec = false;
                            break;
                        }
                    }
                }
                // 44. Przed przejsciem doszukania koljnego wierzcholka sciezki zmieniamy parzystosc.
                parzystosc++;
                licznik++;
            }

            // 45. Sciezka Path zostala skonstrowana. Sciezka moze nie istniec.

            // 46. Jezeli sciezka nie PRZECINA v1.... //nie może przecinać, bo alfa jest kolorem brakujacym v1...

            if (Path.get(Path.size() - 1) != v1.getId())
            {
                // 47. Przesuwamy wachlarz, teraz v1->v2 jest pokolorowana, a v1->xs (tmpVertex) nie
                for (int i = 0; i < FanV1.size() - 1; i++) {
                    // 48. Zmieniamy kolory sciezek w macierzysasiedztwa
                    neighbourhoodMatrix.get(v1.getId()).set(FanV1.get(i), neighbourhoodMatrix.get(v1.getId()).get(FanV1.get(i + 1)));
                    neighbourhoodMatrix.get(FanV1.get(i)).set(v1.getId(), neighbourhoodMatrix.get(v1.getId()).get(FanV1.get(i + 1)));

                    // 49. Zmieniamy kolory odpowiednich sciezek i dodajemy/usuwamy kolory brakujace
                    vertices.get(FanV1.get(i)).addMissingColor(v1.getEdge(FanV1.get(i)).getColor());
                    v1.getEdge(FanV1.get(i)).setColor(neighbourhoodMatrix.get(v1.getId()).get(FanV1.get(i + 1)));
                    vertices.get(FanV1.get(i)).removeMissingColor(v1.getEdge(FanV1.get(i)).getColor());
                }
                // 50. ...Ustalamy kolor krawedzi miedzy v1 a xs(tmpVertex) na 0 (czyli usuwamy ta krawedz)
                v1.getEdge(tmpVertex.getId()).setColor(0);
                // 51. Dodajemy kolor brakujacy tmpVertex (jeszcze nie zmienilismy macierzy sasiedztwa, wiec mozemy w ten sposob)
                tmpVertex.addMissingColor(neighbourhoodMatrix.get(tmpVertex.getId()).get(v1.getId()));
                // 52. Dla v1 i wszystkich wiercholkow nalezacych do wachlarza aktualizujemy kolory brakujace
                v1.setActualMissingColor(degree);
                for (int i = 0; i < FanV1.size(); i++) {
                    vertices.get(FanV1.get(i)).setActualMissingColor(degree);
                    v1.getEdge(FanV1.get(i)).setActualMissingColor(degree);
                }
                // 53. Aktualizujemy macierz sasiedztwa
                neighbourhoodMatrix.get(v1.getId()).set(tmpVertex.getId(), 0);
                neighbourhoodMatrix.get(tmpVertex.getId()).set(v1.getId(), 0);
                // 54. Zakonczylismy przesuwanie wachlarza.
                // 55. Odwracamy kolory utworzonej sciezki Path
                for (int i = 0; i < Path.size() - 1; i++) {
                    // 56. Jezeli dana krawedz sciezki jest koloru alfa, przekolorowywujemy ja na kolor beta
                    if (neighbourhoodMatrix.get(Path.get(i)).get(Path.get(i + 1)) == alfa) {
                        //System.out.println("Przekolorowujemy krawedz " + Path.get(i) + " " + Path.get(i + 1) + " na kolor " + beta);
                        neighbourhoodMatrix.get(Path.get(i)).set(Path.get(i + 1), beta);
                        neighbourhoodMatrix.get(Path.get(i + 1)).set(Path.get(i), beta);
                        vertices.get(Path.get(i)).getEdge(Path.get(i + 1)).setColor(beta);
                        // 57. Dla pierwszego wierzcholka sciezki musimy uaktualnic kolory brakujace: dodac kolor alfa, usunac kolor beta
                        if (i == 0) {
                            vertices.get(Path.get(i)).addMissingColor(alfa);//uaktualniania missingColor
                            vertices.get(Path.get(i)).removeMissingColor(beta);//uaktualniania missingColor
                            vertices.get(Path.get(i)).setActualMissingColor(degree);//nowe
                        }
                        // 58. Jezeli bierzemy ostatni wierzcholek, rowniez musimy uaktualnic kolory brakujace
                        if (i == (Path.size() - 2)) {
                            vertices.get(Path.get(i + 1)).addMissingColor(alfa);//uaktualniania missingColor
                            vertices.get(Path.get(i + 1)).removeMissingColor(beta);//uaktualniania missingColor
                            vertices.get(Path.get(i + 1)).setActualMissingColor(degree);//nowe
                        }
                        // 59. Jezeli dana krawedz sciezki jest koloru beta, przekolorowywujemy ja na kolor alfa
                    } else {
                        neighbourhoodMatrix.get(Path.get(i)).set(Path.get(i + 1), alfa);
                        neighbourhoodMatrix.get(Path.get(i + 1)).set(Path.get(i), alfa);
                        vertices.get(Path.get(i)).getEdge(Path.get(i + 1)).setColor(alfa);
                        // 60. Dla pierwszego wierzcholka sciezki musimy uaktualnic kolory brakujace: dodac kolor alfa, usunac kolor beta
                        // nie powinno sie to NIGDY pojawic, bo pierwsza krawedz sciezki powinna byc koloru ALFA
                        if (i == 0) {
                            //System.out.println("beta i = 0 lub koniec sciezki");
                            vertices.get(Path.get(i)).addMissingColor(beta);//uaktualniania missingColor
                            vertices.get(Path.get(i)).removeMissingColor(alfa);//uaktualniania missingColor
                            vertices.get(Path.get(i)).setActualMissingColor(degree);//nowe
                        }
                        // 61. Jezeli bierzemy ostatni wierzcholek, rowniez musimy uaktualnic kolory brakujace
                        if (i == (Path.size() - 2)) {
                            vertices.get(Path.get(i + 1)).addMissingColor(beta);//uaktualniania missingColor
                            vertices.get(Path.get(i + 1)).removeMissingColor(alfa);//uaktualniania missingColor
                        }
                    }
                }


                // 62. Obrocilismy sciezke.
                // 63. Teraz mozemy pokolorowac v1->xs(tmpVertex). alfa jest kolorem brakujacym v1,
                //     a XS albo nie mial krawedzi koloru alfa, albo przekolorowalismy ja na beta obracajac sciezke
                // 64. Kolorujac v1->tmpVertex kolorem alfa zmieniamy macierz sasiedztwa
                neighbourhoodMatrix.get(v1.getId()).set(tmpVertex.getId(), alfa);
                neighbourhoodMatrix.get(tmpVertex.getId()).set(v1.getId(), alfa);
                // 65. Kolorujemy v1->tmpVertex kolorem alfa
                v1.getEdge(tmpVertex.getId()).setColor(alfa);
                // 66. Aktualizujemy kolory brakujace v1, i XS (tmpVertex)
                v1.removeMissingColor(alfa);//uaktualniania missingColor
                tmpVertex.removeMissingColor(alfa);//uaktualniania missingColor
                v1.setActualMissingColor(degree);//uaktualniania missingColor
                tmpVertex.setActualMissingColor(degree);//uaktualniania missingColor

            }
            // 67. Jezeli sciezka Path PRZECINA v1 //sciezka nie moze przecinac v1, bo alfa jest kolorem brakujacym v1
            else {
                // 68. Szukamy wierzcholka nalezacego do wachlarza, ktory ma kolor brakujacy beta.
                //     Musi taki istniec, poniewaz, w jakis sposob sciezka Path dotarla do V1 - tymczasem alfa jest kolorem brakujacym V1,
                //     wiec sciezka Path musiala dotrzec do V1 kolorem BETA, czyli wierzcholek bedacy w wachlarzu PRZED wierzcholkiem
                //     polaczonym z V1 krawedzia BETA, musi miec kolor brakujacy beta.
                // 	   Gdyby krawedz beta nie nalezala do wachlarza, XS ktorego kolorem brakujacym byla BETA, nie bylby ostatnim
                //     wierzcholkiem wachlarza - możnaby bowiem dodać tę krawędź koloru beta.

                // 69. Niech xi bedzie wierzcholkiem wachlarza takim ze m(xi)=beta
                Vertex xi = new Vertex(-1);
                // 70. W petli szukamy wierzcholka nalezacego do wachlarza o kolorze brakujacym beta, nie bedacego wierzcholkiem XS (tmpVertex)
                //jesli sciezka Path konczy sie w v1, to znaczy, ze ostatnia jej krawedz ma kolor beta
                //wypisywanie macierzy sasiedztwa dla testow
                xi = vertices.get(FanV1.get(FanV1.indexOf(Path.get(Path.size() - 2)) - 1));

                // 71. Konstruujemy sciezke Path2 zaczynajaca sie w xi (ktorego kolor brakujacy to beta) pokolorowana naprzemian kolorami alfa i beta
                //     Beta jest kolorem brakujacym xi, a zatem sciezka z xi musi zaczynac sie kolorem alfa
                ArrayList<Integer> Path2 = new ArrayList<Integer>();
                // 72. Dodajemy xi do sciezki Path2
                Path2.add(xi.getId());
                // 73. Ustalamy xi wierzcholkiem tymczasowo ostatnim sciezki Path2 - tmptmpVertex
                tmptmpVertex = xi;
                licznik = 0;
                koniec = false;
                parzystosc = 0;

                while (licznik < edges.size() && !koniec) {
                    koniec = true;
                    // 74. Zaczynamy od liczby parzysej, zatem najpierw szukamy krawedzi o kolorze alfa (bo beta jest brakujacym kolorem xi)
                    if (parzystosc % 2 == 0) {
                        for (int i = 0; i < vertices.size(); i++) {
                            if (neighbourhoodMatrix.get(tmptmpVertex.getId()).get(i) == alfa && Path2.indexOf(i) < 0) {
                                // 75.  Jezeli znajdziemy odpowiednia krawedz dodajemy ja do sciezki i zmieniamy wierzcholek tymczasowy tmptmpVertex
                                tmptmpVertex = vertices.get(i);
                                Path2.add(i); //Path -> Path2
                                koniec = false;
                                break;
                            }
                        }
                    }
                    // 76. W krokach nieprazystych szukamy krawedzi kolorze beta
                    else {
                        for (int i = 0; i < vertices.size(); i++) {
                            if (neighbourhoodMatrix.get(tmptmpVertex.getId()).get(i) == beta && Path2.indexOf(i) < 0) {
                                // 77.  Jezeli znajdziemy odpowiednia krawedz dodajemy ja do sciezki i zmieniamy wierzcholek tymczasowy tmptmpVertex
                                tmptmpVertex = vertices.get(i);
                                Path2.add(i);  //Path -> Path2
                                koniec = false;
                                break;
                            }
                        }
                    }
                    parzystosc++;
                    licznik++;
                }

                for (int i = 0; i < FanV1.indexOf(xi.getId()); i++)
                {
                    // 80. Przekolorowujemy krawedzie w macierzy sasiedztwa, reagujac na przesuwanie wachlarza
                    neighbourhoodMatrix.get(v1.getId()).set(FanV1.get(i), neighbourhoodMatrix.get(v1.getId()).get(FanV1.get(i + 1)));
                    neighbourhoodMatrix.get(FanV1.get(i)).set(v1.getId(), neighbourhoodMatrix.get(v1.getId()).get(FanV1.get(i + 1)));

                    // 81. Zmieniamy kolory krawedzi przy przesuwaniu wachlarza, aktualizujemy kolory brakujace
                    vertices.get(FanV1.get(i)).addMissingColor(v1.getEdge(FanV1.get(i)).getColor());
                    v1.getEdge(FanV1.get(i)).setColor(neighbourhoodMatrix.get(v1.getId()).get(FanV1.get(i + 1)));
                    vertices.get(FanV1.get(i)).removeMissingColor(v1.getEdge(FanV1.get(i)).getColor());
                }

                // 82. Usuwamy krawedz v1->xi
                v1.getEdge(xi.getId()).setColor(0);
                // 83. Aktualizujemy kolor brakujacy xi, na podstawie macierzy sasiedztwa, ktorej jeszcze nie zmienilismy
                xi.addMissingColor(neighbourhoodMatrix.get(xi.getId()).get(v1.getId()));
                // 84. Aktualizujemy kolory brakujace wierzcholkow wachlarza wlacznie z Xi
                v1.setActualMissingColor(degree);
                for (int i = 0; i < FanV1.indexOf(xi.getId()); i++) {
                    vertices.get(FanV1.get(i)).setActualMissingColor(degree);
                    v1.getEdge(FanV1.get(i)).setActualMissingColor(degree);
                }

                // 85. Aktualizujemy macierz sasiedztwa (usuwamy krawedz v1->xi)
                neighbourhoodMatrix.get(v1.getId()).set(xi.getId(), 0);
                neighbourhoodMatrix.get(xi.getId()).set(v1.getId(), 0);

                // 86. Odwracamy kolory sciezki Path2

                // 87. Przystosc ustawiamy na zero i rozpoczynamy przetwarzanie

                parzystosc = 0;
                for (int i = 0; i < Path2.size() - 1; i++) {
                    // 88. Jezeli jestesmy w kroku parzystym zmieniamy kolor alfa na beta. Pierwszy krok jest wlasnie taki, bo beta jest kolorem brakujacym xi
                    if (parzystosc % 2 == 0) {
                        // 89. Zmieniamy kolor sciezki w macierzy sasiedztwa
                        neighbourhoodMatrix.get(Path2.get(i)).set(Path2.get(i + 1), beta);
                        neighbourhoodMatrix.get(Path2.get(i + 1)).set(Path2.get(i), beta);
//t						//System.out.println("Przekolorowujemy krawedz " + Path2.get(i) + " " + Path2.get(i + 1) + " na kolor " + beta);
                        // 90. Zmieniamy kolor sciezki
                        vertices.get(Path2.get(i)).getEdge(Path2.get(i + 1)).setColor(beta);
                        // 91. Dla pierwszego i ostatniego wierzcholka sciezki musimy aktualizowac kolory brakujace
                        if (i == 0) {
                            //System.out.println("alfa i = 0 " );
                            vertices.get(Path2.get(i)).addMissingColor(alfa);//uaktualniania missingColor
                            vertices.get(Path2.get(i)).removeMissingColor(beta);//uaktualniania missingColor
                            vertices.get(Path2.get(i)).setActualMissingColor(degree);//nowe
                        }
                        if (i == (Path2.size() - 2)) {
                            vertices.get(Path2.get(i + 1)).addMissingColor(alfa);//uaktualniania missingColor
                            //System.out.println("USUWAM");
                            vertices.get(Path2.get(i + 1)).removeMissingColor(beta);//uaktualniania missingColor
                            vertices.get(Path2.get(i + 1)).setActualMissingColor(degree);//nowe
                        }
                    }
                    // 92. Jezeli jestesmy w kroku nieparzystym zmieniamy krawedz koloru beta na alfa
                    else {
                        // 93. Aktualizujemy macierz sasiedztw
                        neighbourhoodMatrix.get(Path2.get(i)).set(Path2.get(i + 1), alfa);
                        neighbourhoodMatrix.get(Path2.get(i + 1)).set(Path2.get(i), alfa);
                        //System.out.println("Przekolorowujemy krawedz P2" + Path2.get(i) + " " + Path2.get(i + 1) + " na kolor " + alfa);
                        // 94. Zmieniamy kolor sciezki
                        vertices.get(Path2.get(i)).getEdge(Path2.get(i + 1)).setColor(alfa);
                        // 95. Dla pierwszego i ostatniego wierzcholka sciezki musimy aktualizowac kolory brakujace
                        if (i == 0) {
                            //System.out.println("beta i = 0 lub koniec sciezki");
                            vertices.get(Path2.get(i)).addMissingColor(beta);//uaktualniania missingColor
                            vertices.get(Path2.get(i)).removeMissingColor(alfa);//uaktualniania missingColor
                            vertices.get(Path2.get(i)).setActualMissingColor(degree);//nowe
                        }
                        if (i == (Path2.size() - 2)) {
                            vertices.get(Path2.get(i + 1)).addMissingColor(beta);//uaktualniania missingColor
                            vertices.get(Path2.get(i + 1)).removeMissingColor(alfa);//uaktualniania missingColor
                            vertices.get(Path2.get(i + 1)).setActualMissingColor(degree);//nowe
                        }
                    }
                    parzystosc++;
                }

                // 96. Kolorujemy sciezke v1->xi kolorem alfa. alfa jest kolorem brakujacym v1, a xi jeżeli mial krawedz alfa
                //     to zmienilismy jej kolor na beta odwracajac sciezke

                // 97. Aktualizujemy macierz sasiedztwa
                neighbourhoodMatrix.get(v1.getId()).set(xi.getId(), alfa);
                neighbourhoodMatrix.get(xi.getId()).set(v1.getId(), alfa);
                // 98. Dodajemy krawedz	(ktorej wczesniej nie bylo)
                v1.getEdge(xi.getId()).setColor(alfa);
                // 99. Aktualizujemy kolory brakujace v1 i xi
                v1.removeMissingColor(alfa);//uaktualniania missingColor
                xi.removeMissingColor(alfa);//uaktualniania missingColor
                v1.setActualMissingColor(degree);//uaktualniania missingColor
                xi.setActualMissingColor(degree);//uaktualniania missingColor
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="OptimalColoring">
    public ColoringResult optimalColor(){
        MemoryHelper memoryHelper = new MemoryHelper();
        memoryHelper.resetPeak();
        long start = System.nanoTime();
        int[] colors = getColorsArray();
        int maxColor = 0;
        for(Edge edge : edges)
        {
            //+1 bo kolory od 1
            int color = colors[edge.getId()] + 1;
            edge.setColor(color);
            if(color > maxColor){
                maxColor = color;
            }
        }
        long end = System.nanoTime();
        long memory = memoryHelper.getPeakUsage();
        return new ColoringResult(maxColor, end - start, memory);
    }

    private int[] getColorsArray(){
        int[] colors = new int[edges.size()];
        int chromaticNumber = getDegree();
        int instancesOfMaxColor = 0;
        int i;
        while (true)
        {
            //jezeli istnieje chociaz jedno kolorowanie o maksymalnym stopniu
            if (instancesOfMaxColor != 0)
            {
                if (isColoringValid(colors))
                {
                    return colors;
                }
            }
            //inkrementacja licznika kolorowania
            while (true)
            {
                for (i = 0; i < edges.size(); i++)
                {
                    colors[i]++;
                    if (colors[i] == chromaticNumber - 1)
                    {
                        instancesOfMaxColor++;
                    }
                    if (colors[i] < chromaticNumber)
                    {
                        break;
                    }
                    colors[i] = 0;
                    instancesOfMaxColor--;
                }
                //i bedzie wieksze rowne edges.size tylko gdy licznik przekroczy aklutalna maksymalna wartosc
                if (i < edges.size())
                {
                    break;
                }
                //zwiekszenie podstawy
                chromaticNumber++;
            }
        }
    }

    private boolean isColoringValid(int[] colors){
        for(int i = 0; i < edges.size(); i++){
            if (!areNeighboursCorrect(i, colors))
            {
                return false;
            }
        }
        return true;
    }

    private boolean areNeighboursCorrect(int index, int[] colors){
        Edge edge = edges.get(index);
        //wszystkie galezie sasiadujace z edge
        List<Edge> neighbouringEdges = new ArrayList<>();
        neighbouringEdges.addAll(edge.getV1().getIncidentEdges());
        neighbouringEdges.addAll(edge.getV2().getIncidentEdges());

        for(Edge neighbouringEdge : neighbouringEdges){
            if(!canEdgesCooexist(edge,neighbouringEdge,colors)){
                return false;
            }
        }
        return true;
    }

    private boolean canEdgesCooexist(Edge first, Edge  second, int[] colors){
        //jezeli maja to samo id lub rozne kolory to jest ok
        return (first.getId() == second.getId()) || (colors[second.getId()] != colors[first.getId()]);
    }

    //</editor-fold>

    public Graph getGraphUncoloredClone(){
        int vertexNumber = vertices.size();
        Graph graph = new Graph();
        graph.addVertices(vertexNumber);
        for(Edge edge: edges){
            graph.addEdge(edge.getV1Id(),edge.getV2Id(),edge.getId());
        }
        for (int i = 0; i < vertexNumber; i++) {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            for (int j = 0; j < vertexNumber; j++) {
                temp.add(0);
            }
            graph.getNeighbourhoodMatrix().add(temp);
        }
        return graph;
    }
}