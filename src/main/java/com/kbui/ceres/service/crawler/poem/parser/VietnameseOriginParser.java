package com.kbui.ceres.service.crawler.poem.parser;

import com.kbui.ceres.exception.crawler.PoemParsingException;
import com.kbui.ceres.service.crawler.poem.entity.Poem;
import com.kbui.ceres.service.crawler.poem.entity.PoemContent;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class VietnameseOriginParser implements PoemParser {
  @Override
  public Poem parseDoc(Document page) throws PoemParsingException {
    String pageTitle = page.select(".page-header h1").html();
    Element poemContentEl = page.select(".poem-content > p").first();
    if (poemContentEl == null) {
      throw new PoemParsingException();
    }
    var poemContent = poemContentEl.html().replace("<br>", "\n");
    return Poem.builder()
        .title(pageTitle)
        .content(List.of(PoemContent.builder().title(pageTitle).content(poemContent).build()))
        .build();
  }
}
