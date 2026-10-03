package com.financemanager.entity;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.LocalDateTime;
@Entity @Table(name="upi_integration_logs")
public class UpiIntegrationLog {
 public enum Provider { PHONEPE, PAYTM, GPAY } public enum Status { SUCCESS, PENDING, FAILED }
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Provider appProvider;
 private String upiId;
 @Column(nullable=false,precision=19,scale=2) private BigDecimal amount;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Status status;
 @Column(columnDefinition="TEXT") private String rawPayload;
 @Column(nullable=false) private LocalDateTime timestamp=LocalDateTime.now();
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false) private User user;
 public UpiIntegrationLog(){} public UpiIntegrationLog(Provider p,String upi,BigDecimal a,Status s,String raw,User u){appProvider=p;upiId=upi;amount=a;status=s;rawPayload=raw;user=u;}
 public Long getId(){return id;} public Provider getAppProvider(){return appProvider;} public BigDecimal getAmount(){return amount;} public Status getStatus(){return status;} public LocalDateTime getTimestamp(){return timestamp;}
}
