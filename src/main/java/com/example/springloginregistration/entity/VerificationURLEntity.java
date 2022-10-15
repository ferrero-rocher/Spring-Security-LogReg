package com.example.springloginregistration.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class VerificationURLEntity {
    private static final int EXPIRATION_TIME =10;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String token;
    private Date expirationTime;
    @OneToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name="user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_USER_VERIFY_URL")
    )
    private UserEntity user;



    public VerificationURLEntity(UserEntity user, String token)
    {
        super();
        this.token=token;
        this.user=user;
        this.expirationTime = calculateExpirationTime(EXPIRATION_TIME);
    }

    public VerificationURLEntity(String url)
    {
        super();
        this.token=url;
        this.expirationTime = calculateExpirationTime(EXPIRATION_TIME);

    }

    private Date calculateExpirationTime(int expirationTime)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE,expirationTime);
        return new Date(calendar.getTime().getTime());
    }



}
