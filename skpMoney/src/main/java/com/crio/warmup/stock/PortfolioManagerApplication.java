package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.SymbolDate;
import com.crio.warmup.stock.dto.TiingoCandle;

//import com.crio.warmup.stock.dto.AnnualizedReturn;
//import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
//import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
//import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
//import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Read the json file provided in the argument[0]. The file will be available in
  // the classpath.
  // 1. Use #resolveFileFromResources to get actual file from classpath.
  // 2. Extract stock symbols from the json file with ObjectMapper provided by
  // #getObjectMapper.
  // 3. Return the list of all symbols in the same order as provided in json.

  // Note:
  // 1. There can be few unused imports, you will need to fix them to make the
  // build pass.
  // 2. You can use "./gradlew build" to check if your code builds successfully.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] trades = objectMapper.readValue(file, PortfolioTrade[].class);
    List<String> symbols = new ArrayList<String>();
    for (PortfolioTrade t : trades) {
      symbols.add(t.getSymbol());

    }
    return symbols;
  }




  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.

  // TODO: CRIO_TASK_MODULE_REST_API
  // Find out the closing price of each stock on the end_date and return the list
  // of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>

  static Comparator<TotalReturnsDto> compare = new Comparator<TotalReturnsDto>() {

    @Override
    public int compare(TotalReturnsDto a, TotalReturnsDto b) {
      return a.getClosingPrice().compareTo(b.getClosingPrice());
    }

  };

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {

    RestTemplate restTemplate = new RestTemplate();
    File file = resolveFileFromResources(args[0]);
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] trades = objectMapper.readValue(file, PortfolioTrade[].class);
    List<SymbolDate> symbols = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy mm dd");
    for (PortfolioTrade t : trades) {
      
      SymbolDate s = new SymbolDate(t.getSymbol(), t.getPurchaseDate().toString());
      symbols.add(s);
    }
    // symbols have all the stock symbols and purchasingDate required

    List<TotalReturnsDto> closingPriceOfAllStocks = new ArrayList<>();

    for (SymbolDate s : symbols) {
      String token = "094697ca86b08f28971657550ab2ab6f7e2ad732";
      String url = "https://api.tiingo.com/tiingo/daily/" + s.getSymbol() + "/prices?startDate=" + s.getPurchaseDate()
          + "&endDate=" + args[1] + "&token=" + token;
      TiingoCandle[] candles = restTemplate.getForObject(url, TiingoCandle[].class);
      List<TiingoCandle> candless = new ArrayList<>();

      for (TiingoCandle a : candles) {
        candless.add(a);
      }
      TiingoCandle t = candless.get(candless.size() - 1);
      Double closingPrice = t.getClose();

      TotalReturnsDto closingPriceOfOneStock = new TotalReturnsDto(s.getSymbol(), closingPrice);
      closingPriceOfAllStocks.add(closingPriceOfOneStock);
    }
    Collections.sort(closingPriceOfAllStocks, compare);
    List<String> ans = new ArrayList<>();
    for (TotalReturnsDto t : closingPriceOfAllStocks) {
      ans.add(t.getSymbol());
    }
    return ans;
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }











  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.


  public static RestTemplate restTemplate=new RestTemplate();
  public static PortfolioManager portfolioManager=PortfolioManagerFactory.getPortfolioManager(restTemplate);
  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args) throws Exception {

        File file=resolveFileFromResources(args[0]);
        LocalDate endDate=LocalDate.parse(args[1]);
        ObjectMapper objectMapper=getObjectMapper();
        PortfolioTrade[] portfolioTrades=objectMapper.readValue(file, PortfolioTrade[].class);
        return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades),endDate);

  //      File file = resolveFileFromResources(args[0]);
  //      LocalDate endDate = LocalDate.parse(args[1]);
       
  //      ObjectMapper objectMapper = getObjectMapper();
  //      PortfolioTrade[] portfolioTrades=objectMapper.readValue(file, PortfolioTrade[].class);

  //      RestTemplate restTemplate=new RestTemplate();
  //      PortfolioManagerFactory factory=new PortfolioManagerFactory();
  //      PortfolioManager portfolioManager= factory.getPortfolioManager(restTemplate);
  //      return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  // }
    
  }






  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    printJsonObject(mainReadQuotes(args));

  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = getObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/aakarsh-kumar-meesho-ME_QMONEY/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@2f9f7dcf";
    String functionNameFromTestFileInStackTrace = "PortfolioManagerApplicationTest.mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "22";

    return Arrays.asList(new String[] { valueOfArgument0, resultOfResolveFilePathArgs0, toStringOfObjectMapper,
        functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace });
  }










  static Comparator<AnnualizedReturn> newCompare = new Comparator<AnnualizedReturn>() {

    @Override
    public int compare(AnnualizedReturn a, AnnualizedReturn b) {
      return b.getAnnualizedReturn().compareTo(a.getAnnualizedReturn());
    }

  };
  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException, DateTimeParseException{

        File file=resolveFileFromResources(args[0]);
        LocalDate endLocalDate=LocalDate.parse(args[1]);

        ObjectMapper objectMapper=getObjectMapper();
        PortfolioTrade[] trades =objectMapper.readValue(file, PortfolioTrade[].class);
        List<AnnualizedReturn> allAnnualizedReturn=new ArrayList<>();
        for(int i=0;i<trades.length;i++){
            allAnnualizedReturn.add(getAnnualizedReturn(trades[i],endLocalDate));
        }
        Comparator<AnnualizedReturn> SortByAnnReturn=Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
        Collections.sort(allAnnualizedReturn,SortByAnnReturn);
        //SORTING LOGIC TO BE WRITTEN HERE
        return allAnnualizedReturn;
  }

  //public static final String token="094697ca86b08f28971657550ab2ab6f7e2ad732";


  private static AnnualizedReturn getAnnualizedReturn(PortfolioTrade trade, LocalDate endLocalDate) {
      String stockSymbol=trade.getSymbol();
      String startDate=trade.getPurchaseDate().toString();  //format(DateTimeFormatter.ofPattern("yyyy dd mm"));
      String s=endLocalDate.toString();   //format(DateTimeFormatter.ofPattern("yyyy dd mm"));
      LocalDate s1=trade.getPurchaseDate();
      if(s1.compareTo(endLocalDate)>=0){
          throw new RuntimeException();
      }
      String token="094697ca86b08f28971657550ab2ab6f7e2ad732";

      String url = "https://api.tiingo.com/tiingo/daily/" +stockSymbol + "/prices?startDate=" + startDate+"&endDate=" + s + "&token=" + token;

      RestTemplate restTemplate=new RestTemplate();
      
      TiingoCandle[] candles=restTemplate.getForObject(url, TiingoCandle[].class);

      if(candles!=null){
        TiingoCandle startDateStock= candles[0];
        TiingoCandle endDateStock=candles[candles.length-1];

        Double buyPrice= startDateStock.getOpen();
        Double sellPrice= endDateStock.getClose();
        AnnualizedReturn annualizedReturn= calculateAnnualizedReturns(endLocalDate, trade, buyPrice, sellPrice);
        return annualizedReturn;
      }
      else{
        return new AnnualizedReturn(stockSymbol,Double.NaN,Double.NaN);
      }
  }
  public static AnnualizedReturn calculateAnnualizedReturns(
      LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
        Double numberofYears=(double)ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate)/365;

        Double tir= ((sellPrice-buyPrice)/(buyPrice))+1;
        Double annualizedYear= 1/((double)numberofYears);
        Double annualizedReturn= Math.pow(tir,annualizedYear)-1;
        return new AnnualizedReturn(trade.getSymbol(), annualizedReturn, tir);
  }






  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }



  

    //printJsonObject(mainCalculateReturnsAfterRefactor(args){});
  }


