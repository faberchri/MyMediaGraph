# MyMediaGraph

A fork of the [MyMediaLite](http://mymedialite.net) framework for recommendation algorithm evaluation with mainly two additions:
* A library of graph vertex ranking recommendation algorithms
* Various diversity metrics (`my-media-lite-4j/src/main/java/org/mymedialite/eval/measures/diversity`)

## How to use
* Build the project with maven. Don't run the tests, takes too long. 
   ```bash
   mvn clean package -DskipTests
   ```

* Run a recommender on the included MovieLens dataset, here the `Most Popular`-algo (in the project root dir):
   ```bash
   java -jar my-media-main/target/my-media-main-1.0-SNAPSHOT-jar-with-dependencies.jar MostPopular data/ml_medium_7-3_split train.csv test.csv attributes.csv
   ```

* Run a recommender (`BmANP3`) with a particular parameter set:
   ```bash
   java -jar my-media-main/target/my-media-main-1.0-SNAPSHOT-jar-with-dependencies.jar "BmANP3{numWalks=100000,beta=0.9}" data/ml_medium_7-3_split train.csv test.csv attributes.csv
   ```

* Get the recommendation quality statistics from `data/ml_medium_7-3_split/METRICS.csv`.

A list of the included recommenders together with the corresponding identifiers is available at `MyMediaGraph/my-media-main/src/main/java/ch/uzh/ifi/ddis/mymedialite/main/ItemRecommenderEvaluate.java`. To test additional recommenders, expand the list with classes implementing the ItemRecommender interface.  

## Publication
Our RecSys'15 paper presenting accuracy and diversity performance of vertex ranking recommendation algorithms: [Blockbusters and Wallflowers: Accurate, Diverse, and Scalable Recommendations with Random Walks](http://dl.acm.org/citation.cfm?doid=2792838.2800180)

Mapping of recommendation algorithm names:

| Name in Paper | Command line `Identifier`           | 
----------------|-------------------------------------|
| Perfect       | `Perfect`                             |
| Random        | `Random`                              |
| MostPop       | `MostPopular`                         |
| I-kNN         | `ItemKNN`                             |
| WI-kNN        | `WeightedItemKNN`                     |
| BPRMF         | `BPRMF2`                              |
| #3-Paths      | `IB-3Path`                            |
| ![\mathrm{L^{+}}](https://latex.codecogs.com/gif.latex?%5Cmathrm%7BL%5E%7B&plus;%7D%7D)                                        | `Laplacian`                           |
| ![\mathrm{P^{3}}](https://latex.codecogs.com/gif.latex?%5Cmathrm%7BP%5E%7B3%7D%7D)                                             | `P3Matrix`                            |
| ![\mathrm{\hat{P}^{3}}](https://latex.codecogs.com/gif.latex?%5Cmathrm%7B%5Chat%7BP%7D%5E%7B3%7D%7D)                           | `P3RandomWalkCached`                  |
| ![\mathrm{P^{3}_{\alpha}}](https://latex.codecogs.com/gif.latex?%5Cmathrm%7BP%5E%7B3%7D_%7B%5Calpha%7D%7D)                     | `P3AlphaMatrix`                       |
| ![\mathrm{\hat{P}^{3}_{\alpha}}](https://latex.codecogs.com/gif.latex?%5Cmathrm%7B%5Chat%7BP%7D%5E%7B3%7D_%7B%5Calpha%7D%7D)   | `BmP3Alpha`                           |
| ![\mathrm{P^{5}}](https://latex.codecogs.com/gif.latex?%5Cmathrm%7BP%5E%7B5%7D%7D)                                             | `P5Matrix`                            |
| ![\mathrm{RP^{3}_{\beta}}](https://latex.codecogs.com/gif.latex?%5Cmathrm%7BRP%5E%7B3%7D_%7B%5Cbeta%7D%7D)                     | `P3PopAbsNorm`                        |
| ![\mathrm{R\hat{P}^{3}_{\beta}}](https://latex.codecogs.com/gif.latex?%5Cmathrm%7BR%5Chat%7BP%7D%5E%7B3%7D_%7B%5Cbeta%7D%7D)   | `BmANP3`                              |
| ![\mathrm{H_{\lambda}}](https://latex.codecogs.com/gif.latex?%5Cmathrm%7BH_%7B%5Clambda%7D%7D)                                 | `HybridS`                             |
| ![\mathrm{\hat{H}_{\lambda}}](https://latex.codecogs.com/gif.latex?%5Cmathrm%7B%5Chat%7BH%7D_%7B%5Clambda%7D%7D)               | `BmHybridS`                           |

## Related projects

[MyMediaLite project](http://github.com/zenogantner/mymedialite)

[Java port of MyMediaLite](https://github.com/jcnewell/MyMediaLiteJava)