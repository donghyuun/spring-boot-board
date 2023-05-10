package com.example.sbb;

import org.springframework.stereotype.Component;
import org.commonmark.node.Node;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.parser.Parser;

@Component//스프링부트에 의해 관리되는 빈(bean, 자바객체)으로 등록된다. "빈으로 등록된 컴포넌트는 템플릿에서 바로 사용할 수 있다!!!"
public class CommonUtil {
    public String markdown(String markdown){//마크다운 텍스트를 HTML 문서로 변환하여 리턴한다.
                                            //즉 마크다운 문법이 적용된 일반텍스트를 변환된(소스코드, 기울기, 굵기, 링크 등) HTML로 리턴한다.
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

}
