package ceres.crawler;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PoemFetcher {
  private final String poetName;

  public PoemFetcher(String poetName) {
    this.poetName = poetName;
  }

  private String getPoetPage() throws IOException {
    String url = String.format("https://www.thivien.net/qsearch.xml.php"
        + "?Core=author&Field=Name&Value=%s&Page=0", URLEncoder.encode(this.poetName));
    Document page = BaseCrawler.fetchPage(url);
    Elements links = page.select("a");
    return String.format("https://www.thivien.net/%s", links.get(0).attr("href"));
  }

  private List<String> getPoemLinks() throws IOException {
    String poetUrl = this.getPoetPage();
    Document page = BaseCrawler.fetchPage(poetUrl);
    Elements elements = page.select(".poem-group-list li a");
    return elements.stream()
        .map(el -> String.format("https://www.thivien.net/%s", el.attr("href")))
        .collect(Collectors.toList());
  }

  public List<Poem> getPoems() throws IOException {
    List<String> links = getPoemLinks();
    return links.stream().map(l -> {
      try {
        return getPoem(l);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }).collect(Collectors.toList());
  }

  private Poem getPoem(String link) throws IOException {
    Document page = BaseCrawler.fetchPage(link);
    Elements poemTitlesEls = page.select(".poem-view-separated > h4");
    Elements poemContentEls = page.select(".poem-view-separated > p");
    String pageTitle = page.select(".page-header h1").html();
    List<String> poemContents = poemContentEls
        .stream()
        .filter(el -> el.text().isEmpty())
        .map(el -> el.html().replace("<br>", "\n"))
        .collect(Collectors.toList());
    List<PoemContent> poems = new ArrayList<PoemContent>();
    var index = 0;
    for (Element title : poemTitlesEls) {
      var poemContent = poemContents.get(index);
      var poem = PoemContent.builder()
          .title(title.text())
          .content(poemContent)
          .build();
      poems.add(poem);
    }
    return Poem.builder()
        .title(pageTitle)
        .content(poems)
        .build();
  }
}
