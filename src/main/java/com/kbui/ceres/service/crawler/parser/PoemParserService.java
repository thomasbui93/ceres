package com.kbui.ceres.service.crawler.parser;

import com.kbui.ceres.exception.crawler.PoemParsingException;
import com.kbui.ceres.service.crawler.entity.Poem;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class PoemParserService {
  public static Poem parse(Document page) throws PoemParsingException {
    var parser = getPoemParser(page);
    return parser.parseDoc(page);
  }

  protected static PoemParser getPoemParser(Document page) {
    Elements poemContentEls = page.select(".poem-view-separated > p");
    return poemContentEls.size() == 0 ? new VietnameseOriginParser() : new ChineseOriginParser();
  }
}
