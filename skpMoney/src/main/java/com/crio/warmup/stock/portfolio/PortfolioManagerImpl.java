
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {


RestTemplate restTemplate;
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.








  
  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
        
    
      AnnualizedReturn annualizedReturn;
      List<AnnualizedReturn> annualizedReturns=new ArrayList<AnnualizedReturn>();
      for(int i=0;i<portfolioTrades.size();i++){
        annualizedReturn=getAnnualizedReturn(portfolioTrades.get(i),endDate);
        annualizedReturns.add(annualizedReturn);
      }
      Comparator<AnnualizedReturn> SortByAnnReturn=Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
      Collections.sort(annualizedReturns,SortByAnnReturn);
      return annualizedReturns;





    //File file=resolveFileFromResources(args[0]);
        //LocalDate endLocalDate=LocalDate.parse(args[1]);

        //ObjectMapper objectMapper=getObjectMapper();
        //PortfolioTrade[] trades =objectMapper.readValue(file, PortfolioTrade[].class);
        // List<AnnualizedReturn> allAnnualizedReturn=new ArrayList<>();

        // for(PortfolioTrade trade:portfolioTrades){
        //   allAnnualizedReturn.add(getAnnualizedReturn(trade,endDate));
        // }
        // // for(int i=0;i<trades.length;i++){
        // //     allAnnualizedReturn.add(getAnnualizedReturn(trades[i],endLocalDate));
        // // }
        // Comparator<AnnualizedReturn> SortByAnnReturn=Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
        // Collections.sort(allAnnualizedReturn,SortByAnnReturn);
        // //SORTING LOGIC TO BE WRITTEN HERE
        // return allAnnualizedReturn;
  }

  private AnnualizedReturn getAnnualizedReturn(PortfolioTrade trade, LocalDate endLocalDate) {
      AnnualizedReturn annualizedReturn;
      String symbol=trade.getSymbol();
      LocalDate startLocalDate=trade.getPurchaseDate();
      try{
        List<Candle> stocksStartToEndDate;
        stocksStartToEndDate=getStockQuote(symbol,startLocalDate,endLocalDate);

        Candle stockStartDate=stocksStartToEndDate.get(0);
        Candle stockLatest=stocksStartToEndDate.get(stocksStartToEndDate.size()-1);
        Double buyPrice=stockStartDate.getOpen();
        Double sellPrice=stockLatest.getClose();

        Double totalReturn= (sellPrice-buyPrice)/buyPrice;
        Double numYears=(double)ChronoUnit.DAYS.between(startLocalDate, endLocalDate)/365;
        Double annualizedReturns=Math.pow((1+totalReturn),(1/numYears))-1;
        annualizedReturn=new AnnualizedReturn(symbol,annualizedReturns,totalReturn);

      }catch(JsonProcessingException e){
        annualizedReturn=new AnnualizedReturn(symbol, Double.NaN,Double.NaN);
      }
      return annualizedReturn;


  //   String stockSymbol=trade.getSymbol();
  //   String startDate=trade.getPurchaseDate().toString();  //format(DateTimeFormatter.ofPattern("yyyy dd mm"));
  //   String s=endLocalDate.toString();   //format(DateTimeFormatter.ofPattern("yyyy dd mm"));
  //   LocalDate s1=trade.getPurchaseDate();
  //   AnnualizedReturn annualizedReturn;
  //  try{
  //     // String token="094697ca86b08f28971657550ab2ab6f7e2ad732";

  //     // String url = "https://api.tiingo.com/tiingo/daily/" +stockSymbol + "/prices?startDate=" + startDate+"&endDate=" + s + "&token=" + token;
  //   String url= buildUri(stockSymbol, s1, endLocalDate);

  //   RestTemplate restTemplate = new RestTemplate();

  //   TiingoCandle[] candles = restTemplate.getForObject(url, TiingoCandle[].class);
    
  //     TiingoCandle startDateStock = candles[0];
  //     TiingoCandle endDateStock = candles[candles.length - 1];

  //     Double buyPrice = startDateStock.getOpen();
  //     Double sellPrice = endDateStock.getClose();
  //     annualizedReturn = calculateAnnualizedReturnssss(endLocalDate, trade, buyPrice, sellPrice);
  //   } catch(JsonProcessingException e) {
  //     annualizedReturn=new AnnualizedReturn(stockSymbol,Double.NaN,Double.NaN);
  //   }
  //   return annualizedReturn;
  }

  public static AnnualizedReturn calculateAnnualizedReturnssss(LocalDate endDate, PortfolioTrade trade, Double buyPrice,
      Double sellPrice) {
    Double numberofYears = (double) ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate) / 365;

    Double tir = ((sellPrice - buyPrice) / (buyPrice)) + 1;
    Double annualizedYear = 1 / ((double) numberofYears);
    Double annualizedReturn = Math.pow(tir, annualizedYear) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualizedReturn, tir);
  }

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command
  // below:
  // ./gradlew test --tests PortfolioManagerTest

  // CHECKSTYLE:OFF

  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  // CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Extract the logic to call Tiingo third-party APIs to a separate function.
  // Remember to fill out the buildUri function and use that.

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException{


        if(from.compareTo(to)>=0){
          throw new RuntimeException();
        }
        String url=buildUri(symbol,from,to);
        TiingoCandle[] stocksStartToEndDate=restTemplate.getForObject(url, TiingoCandle[].class);
        if(stocksStartToEndDate==null){
          return new ArrayList<Candle>();
        }
        else{
        List<Candle> stock=Arrays.asList(stocksStartToEndDate);
        return stock;
        }



    //   throws JsonProcessingException {
    //     if(startDate.compareTo(endDate)>=0){
    //       throw new RuntimeException();

    //     }
    // String url=buildUri(symbol, startDate, endDate);
    // RestTemplate rest=new RestTemplate();
    // TiingoCandle[] candles = rest.getForObject(url, TiingoCandle[].class);
    // if(candles==null) return new ArrayList<TiingoCandle>();
    // else{
    //   List<TiingoCandle> stock=Arrays.asList(candles);
    //   return stock;
    // }
  }

  protected static String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String token = "094697ca86b08f28971657550ab2ab6f7e2ad732";
    String url = "https://api.tiingo.com/tiingo/daily/" +symbol + "/prices?startDate=" + startDate+"&endDate=" + endDate + "&token=" + token;
    return url;
  }

 
}
