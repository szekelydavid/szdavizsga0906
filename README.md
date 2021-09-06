# Receptgyűjtemény

A feladatod egy online szakácskönyv backendjének kidolgozása. A hozzávalókat és az őket felhasználó 
recepteket relációs adatbázisban kell tárolni. Az automata, integrációs teszteseteket készen kapod. 
Ezek futtatásához először létre kell hoznod a modelleket (**fontos**, hogy a megadott package-ben hozd őket létre!), 
majd implementálnod kell a meghatározott API végpontokat.


A feladat megoldását a Clean Code elvek betartásával, rétegekre bontva kell biztosítanod.

A megoldáshoz csatolj konténerizációhoz szükséges fájlokat is (dockerfile, futtatáshoz szükséges parancs)!

## Modellek
- package: com.codecool.cookbook.model

### IngredientType
- Enum, a következő opciókkal: `MEAT, VEGETABLE, FRUIT, DAIRY, FISH, OTHER`

### Ingredient
- id: `Long`
- name: `String`
- ingredientType: `IngredientType`

### Recipe
- id: `Long`
- name: `String`
- ingredients: `Ingredient` objektumokat tartalmazó `List`

## API Végpontok
* Minden végpont JSON formátumban kommunikál.
* Minden olyan végpont, amely tömbbel válaszol, az adatbázishoz való hozzáadás sorrendjében adja vissza az adatokat, 
találat híján pedig üres tömbbel tér vissza


- `GET /ingredient`: visszaadja az adatbázisban tárolt összes hozzávalót egy tömbben.
- `GET /ingredient/{id}`: visszaadja a megadott id-val rendelkező hozzávalót. Amennyiben nem szerepel 
az adatbázisban hozzávaló a megadott id-val, _500-as HTTP kóddal_ válaszol (pl. `RuntimeException`).
- `POST /ingredient`: a _request body_-jában fogad egy `Ingredient`-et JSON formátumban, lementi az adatbázisba, 
  majd visszaadja válaszként a lementett entitást. Fontos: az elmenteni kívánt hozzávaló **nem** tartalmazhat id-t! 
  Ha mégis ilyen kérés érkezik, akkor _500-as HTTP kóddal_ válaszol.
- `PUT /ingredient`: a _request body_-jában fogad egy `Ingredient`-et JSON formátumban, _id-val együtt_. 
  A megadott id-val rendelkező entitást felülírja a kapott adatokkal. Amennyiben a kérésben elküldött hozzávaló
  nem tartalmaz id-t, vagy nem létező id-t tartalmaz, nem történik felülírás.
- `DELETE /ingredient/{id}`: törli az adatbázisból a megadott id-jú hozzávalót, kivéve, ha az szerepel legalább egy receptben.


- `GET /recipe`: visszaadja az adatbázisban tárolt összes receptet egy tömbben.
- `GET /recipe/{id}`: visszaadja a megadott id-val rendelkező receptet. Amennyiben nem szerepel
  az adatbázisban recept a megadott id-val, _500-as HTTP kóddal_ válaszol (pl. `RuntimeException`).
- `POST /recipe`: a _request body_-jában fogad egy `Recipe`-t JSON formátumban, lementi az adatbázisba,
  majd visszaadja válaszként a lementett entitást. Fontos: az elmenteni kívánt recept **nem** tartalmazhat id-t 
  és/vagy előzetesen nem lementett hozzávalókat!
  Ha mégis ilyen kérés érkezik, akkor _500-as HTTP kóddal_ válaszol. 
- `PUT /recipe`: a _request body_-jában fogad egy `Recipe`-t JSON formátumban, _id-val együtt_.
  A megadott id-val rendelkező entitást felülírja a kapott adatokkal. Amennyiben a kérésben elküldött recept
  előzetesen nem lementett hozzávalót tartalmaz, _500-as HTTP kóddal_ válaszol. Amennyiben nem tartalmaz id-t, vagy 
  nem létező id-t tartalmaz, nem történik felülírás. 
- `DELETE /recipe/{id}`: törli az adatbázisból a megadott id-jú receptet (a hozzávalókat nem!).
- `GET /recipe/vegetarian`: visszaadja egy tömbben az adatbázisban tárolt összes olyan receptet, 
  amely nem tartalmaz húst (`MEAT ingredientType`-al rendelkező hozzávaló).
- `GET /recipe/non-dairy`: visszaadja egy tömbben az adatbázisban tárolt összes olyan receptet, 
  amely nem tartalmaz tejterméket (`DAIRY ingredientType`-al rendelkező hozzávaló).
  