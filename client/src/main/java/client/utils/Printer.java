package client.utils;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.Unit;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Printer {
    /**
     * Transforms a Recipe object into a String with markdown notation.
     *
     * @param recipe {@link Recipe} object which is to be saved in markdown
     * @return String with markdown formatted description of the recipe
     */
    public String recipePrint(Recipe recipe, List<RecipeIngredient> recipeIngredients) {
        StringBuilder output = new StringBuilder();
        output.append("## ").append(recipe.getName()).append("\n\n");
        output.append("\n**Servings:** ").append(recipe.getServings());
        output.append("\n\n**Ingredients:**");
        for(RecipeIngredient ing : recipeIngredients) {
            output.append("\n - ").append(ing.getIngredient().getName());
            String amount = new BigDecimal(Double.toString(ing.getAmount()))
                    .stripTrailingZeros()
                    .toPlainString();
            output.append(" - ").append(amount.equals("0") ? "" : amount).append(" ");
            String unit =
                    ing.getUnit()==Unit.CUSTOM ? ing.getInformalUnit() : ing.getUnit().toString();
            output.append(unit==null ? "" : unit);
        }
        output.append("\n\n**Preparation steps:**");
        for(int i=1; i<=recipe.getPreparationSteps().size(); i++) {
            output.append("\n").append(i).append(". ");
            output.append(recipe.getPreparationSteps().get(i-1));    // Add auto line-breaks
        }
        output.append("\n\nHAVE A GOOD MEAL!!");

        return output.toString();
    }

    /**
     * Transforms a String with Markdown formatting into a pdf file and
     * saves it at a specified location.
     * @param path Where to save the file. Needs to end with ".pdf"
     * @param markdown String with the markdown content
     * @throws IOException If the saving fails
     */
    public void markdownToPDF(Path path, String markdown) throws IOException {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Node node = parser.parse(markdown);
        String html = renderer.render(node);

        String xhtml =
                        """
                        <!DOCTYPE html>
                        <html>
                        <head>" +
                        <meta charset="utf-8" />
                        <style>
                            body { font-family: Verdana; font-size: 25px; }
                            h1 { color: #333; }
                            p { line-height: 1.5; }
                        </style>
                        </head>
                        <body>""" +
                        html +
                        """
                        </body>
                        </html>""";

        Files.createDirectories(path.getParent());

        try (OutputStream os = Files.newOutputStream(path)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(xhtml, null);
            builder.toStream(os);
            builder.run();
        }

    }

}
