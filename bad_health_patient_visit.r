ibrary("COUNT")

data("badhealth")
?badhealth
head(badhealth)

any(is.na(badhealth))

hist(badhealth$numvisit, breaks=20)

plot(jitter(log(numvisit)) ~ jitter(age), data=badhealth, subset=badh==0, xlab="age", ylab="log(visits)")
points(jitter(log(numvisit)) ~ jitter(age), data=badhealth, subset=badh==1, col="red")

library("rjags")

mod_patient_visit_string = " model {
    for (i in 1:length(numvisit)) {
        numvisit[i] ~ dpois(lam[i])
        log(lam[i]) = int + b_badh*badh[i] + b_age*age[i] + b_intx*age[i]*badh[i]
    }
    
    int ~ dnorm(0.0, 1.0/1e6)
    b_badh ~ dnorm(0.0, 1.0/1e4)
    b_age ~ dnorm(0.0, 1.0/1e4)
    b_intx ~ dnorm(0.0, 1.0/1e4)
} "

set.seed(102)

data_jags_patient_visit = as.list(badhealth)

mod_patient_visit = jags.model(textConnection(mod_patient_visit_string), data=data_jags_patient_visit, n.chains=3)
update(mod_patient_visit, 1e3)

params_mod_patient_visit = c("int", "b_badh", "b_age", "b_intx")

mod_sim_patient_visit = coda.samples(model=mod_patient_visit,
                        variable.names=params_mod_patient_visit,
                        n.iter=5e3)
mod_csim_patient_visit = as.mcmc(do.call(rbind, mod_sim_patient_visit))

## convergence diagnostics
plot(mod_csim_patient_visit)

gelman.diag(mod_csim_patient_visit)
autocorr.diag(mod_csim_patient_visit)
autocorr.plot(mod_csim_patient_visit)
effectiveSize(mod_csim_patient_visit)

## compute DIC
dic = dic.samples(mod_patient_visit, n.iter=1e3)

X = as.matrix(badhealth[,-1])
X = cbind(X, with(badhealth, badh*age))
head(X)



(pmed_coef = apply(mod_csim_patient_visit, 2, median))


llam_hat = pmed_coef["int"] + X %*% pmed_coef[c("b_badh", "b_age", "b_intx")]
lam_hat = exp(llam_hat)

hist(lam_hat)

resid = badhealth$numvisit - lam_hat
plot(resid) # the data were ordered

plot(lam_hat[which(badhealth$badh==0)], resid[which(badhealth$badh==0)], xlim=c(0, 8), ylab="residuals", xlab=expression(hat(lambda)), ylim=range(resid))
points(lam_hat[which(badhealth$badh==1)], resid[which(badhealth$badh==1)], col="red")

var(resid[which(badhealth$badh==0)])

var(resid[which(badhealth$badh==1)])

summary(mod_csim_patient_visit)

x1 = c(0, 35, 0) # good health
x2 = c(1, 35, 35) # bad health

head(mod_csim_patient_visit)

loglam1 = mod_csim_patient_visit[,"int"] + mod_csim_patient_visit[,c(2,1,3)] %*% x1
loglam2 = mod_csim_patient_visit[,"int"] + mod_csim_patient_visit[,c(2,1,3)] %*% x2

lam1 = exp(loglam1)
lam2 = exp(loglam2)

(n_sim = length(lam1))

y1 = rpois(n=n_sim, lambda=lam1)
y2 = rpois(n=n_sim, lambda=lam2)

plot(table(factor(y1, levels=0:18))/n_sim, pch=2, ylab="posterior prob.", xlab="visits")
points(table(y2+0.1)/n_sim, col="red")

mean(y2 > y1)



mod_patient_visit_string_simple = " model {
    for (i in 1:length(numvisit)) {
        numvisit[i] ~ dpois(lam[i])
        log(lam[i]) = int + b_badh*badh[i] + b_age*age[i]
    }
    
    int ~ dnorm(0.0, 1.0/1e6)
    b_badh ~ dnorm(0.0, 1.0/1e4)
    b_age ~ dnorm(0.0, 1.0/1e4)
} "

params_mod_patient_visit_simple = c("int", "b_badh", "b_age")

mod_patient_visit_simple = jags.model(textConnection(mod_patient_visit_string_simple), data=data_jags_patient_visit, n.chains=3)

mod_sim_patient_visit_simple = coda.samples(model=mod_patient_visit_simple,
                        variable.names=params_mod_patient_visit_simple,
                        n.iter=5e3)
						
## convergence diagnostics
plot(mod_sim_patient_visit_simple)

gelman.diag(mod_sim_patient_visit_simple)
autocorr.diag(mod_sim_patient_visit_simple)
autocorr.plot(mod_sim_patient_visit_simple)
effectiveSize(mod_sim_patient_visit_simple)

## compute DIC
dic_simple = dic.samples(mod_patient_visit_simple, n.iter=1e3)						