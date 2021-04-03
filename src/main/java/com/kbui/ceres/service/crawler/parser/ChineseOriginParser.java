package com.kbui.ceres.service.crawler.parser;

import com.kbui.ceres.service.crawler.Poem;
import com.kbui.ceres.service.crawler.PoemContent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ChineseOriginParser implements PoemParser {
  @Override
  public Poem parseDoc(Document page) {
    Elements poemTitlesEls = page.select(".poem-view-separated > h4");
    Elements poemContentEls = page.select(".poem-view-separated > p");
    String pageTitle = page.select(".page-header h1").html();
    List<String> poemContents =
        poemContentEls.stream()
            .filter(el -> !el.text().isEmpty())
            .map(el -> el.html().replace("<br>", "\n"))
            .collect(Collectors.toList());
    List<PoemContent> poems = new ArrayList<>();
    var index = 0;
    for (Element title : poemTitlesEls) {
      var poemContent = poemContents.get(index);
      var poem = PoemContent.builder().title(title.text()).content(poemContent).build();
      poems.add(poem);
      index++;
    }
    return Poem.builder().title(pageTitle).content(poems).build();
  }
}
