package com.distopy.service.impl;

import com.distopy.service.CommomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CommomServiceImpl implements CommomService {

    /**
     * Este método remove as mensagens de sucesso e erro da sessão do usuário.
     *
     * Funcionamento:
     * 1. Obtém a requisição HTTP atual do contexto do Spring usando RequestContextHolder.
     * 2. Recupera a sessão ativa (HttpSession) associada à requisição.
     * 3. Remove os atributos "successMsg" e "errorMsg" da sessão, caso existam.
     *
     * Uso típico:
     * - Após exibir mensagens de sucesso ou erro para o usuário em uma página,
     *   este método é chamado para limpar a sessão e evitar que as mensagens
     *   sejam exibidas novamente em futuras requisições.
     *
     * Observação:
     * - Se a sessão ainda não existir, o método getSession() irá criá-la.
     * - Caso prefira evitar a criação de uma nova sessão desnecessariamente,
     *   poderia-se usar getSession(false).
     */
    @Override
    public void removeSessionMessage() {
        HttpServletRequest request = ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes()))
                .getRequest();
        HttpSession session = request.getSession();
        session.removeAttribute("successMsg");
        session.removeAttribute("errorMsg");

    }
}
