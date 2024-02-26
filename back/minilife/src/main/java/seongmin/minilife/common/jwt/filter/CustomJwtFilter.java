package seongmin.minilife.common.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import seongmin.minilife.common.exception.AuthErrorException;
import seongmin.minilife.common.auth.service.CustomUserDetailsService;
import seongmin.minilife.common.code.AuthErrorCode;
import seongmin.minilife.common.jwt.provider.TokenProvider;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomJwtFilter extends OncePerRequestFilter {
    private final TokenProvider accessTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    private final List<String> jwtIgnoreUrl = List.of(
            "/", "/favicon.ico",
            "/swagger", "/swagger-ui/**", "/v3/api-docs/**",
            "/oauth2/**", "/api/v1/users/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (ignoreTokenRequest(request)) {
            log.info("JWT Filter: Ignoring request: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if (isAnonymousRequest(request)) {
            log.info("JWT Filter: Anonymous request: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = resolveAccessToken(request, response);

        UserDetails userDetails = getUserDetails(accessToken);
        setAuthenticationUser(userDetails, request);

        filterChain.doFilter(request, response);
    }

    private boolean isAnonymousRequest(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");

        return StringUtils.hasText(accessToken);
    }

    private boolean ignoreTokenRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        Optional<String> judge = jwtIgnoreUrl.stream()
                .filter(v ->
                        Pattern.matches(v.replace("**", ".*"), uri) ||
                                Pattern.matches(v.replace("/**", ""), uri)

                )
                .findFirst();
        return judge.isPresent() || "OPTIONS".equals(method);
    }

    private String resolveAccessToken(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String accessToken = accessTokenProvider.resolveToken(request);

        log.warn("액세스 토큰 : {}", accessToken);
        if (!StringUtils.hasText(accessToken)) {
            handleAuthErrorException(AuthErrorCode.EMPTY_ACCESS_TOKEN, "엑세스 토큰이 비어있습니다.");
        }

        if (Boolean.TRUE.equals(accessTokenProvider.isTokenExpire(accessToken))) {
            handleAuthErrorException(AuthErrorCode.EXPIRED_ACCESS_TOKEN, "만료된 엑세스 토큰입니다.");
        }

        return accessToken;
    }

    private UserDetails getUserDetails(String accessToken) {
        Long userId = accessTokenProvider.getIdFromToken(accessToken);
        return customUserDetailsService.loadUserByUsername(userId.toString());
    }

    private void setAuthenticationUser(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Authenticated user: {}", userDetails.getUsername());
    }

    /**
     * JwtFilter 내에서 에러 발생 시 에러 응답 반환
     */
    private void handleAuthErrorException(AuthErrorCode errorCode, String errorMessage) throws ServletException {
        log.warn("AuthErrorException: {}@{}", errorCode.name(), errorMessage);
        AuthErrorException exception = new AuthErrorException(errorCode, errorMessage);
        throw new ServletException(exception);
    }
}