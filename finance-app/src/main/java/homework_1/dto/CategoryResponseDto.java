package homework_1.dto;

public class CategoryResponseDto {
    public CategoryResponseDto(String categoryReport) {
        this.categoryReport = categoryReport;
    }

    public String getCategoryReport() {
        return categoryReport;
    }

    public void setCategoryReport(String categoryReport) {
        this.categoryReport = categoryReport;
    }

    private String categoryReport;
}
