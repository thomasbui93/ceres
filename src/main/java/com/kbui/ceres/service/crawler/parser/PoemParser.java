package com.kbui.ceres.service.crawler.parser;

import com.kbui.ceres.exception.crawler.PoemParsingException;
import com.kbui.ceres.service.crawler.Poem;
import org.jsoup.nodes.Document;

public interface PoemParser {
  Poem parseDoc(Document page) throws PoemParsingException;
}
