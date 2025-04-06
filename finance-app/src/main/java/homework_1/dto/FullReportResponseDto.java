package homework_1.dto;

public class FullReportResponseDto {
    private String fullReport;

    public String getFullReport() {
        return fullReport;
    }

    public void setFullReport(String fullReport) {
        this.fullReport = fullReport;
    }

    public FullReportResponseDto(String fullReport) {
        this.fullReport = fullReport;
    }
}
