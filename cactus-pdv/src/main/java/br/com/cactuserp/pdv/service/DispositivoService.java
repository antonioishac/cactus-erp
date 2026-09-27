package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.DispositivoCaixa;
import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.repository.DispositivoCaixaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DispositivoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispositivoService.class);

    private final DispositivoCaixaRepository dispositivoRepository;
    private final DispositivoInfoService dispositivoInfo;

    public DispositivoService(DispositivoCaixaRepository dispositivoRepository,
                              DispositivoInfoService dispositivoInfo) {
        this.dispositivoRepository = dispositivoRepository;
        this.dispositivoInfo = dispositivoInfo;
    }

    /**
     * Tenta identificar o caixa vinculado ao dispositivo atual (pelo hostname).
     * Retorna vazio se for a primeira vez que essa máquina é usada.
     */
    public Optional<DispositivoCaixa> identificarDispositivoAtual() {
        String hostname = dispositivoInfo.getHostname();
        return dispositivoRepository.findAtivoByHostname(hostname);
    }

    /**
     * Vincula esta máquina (hostname/MAC) a um caixa.
     * Usado no primeiro login para gravar a associação.
     */
    @Transactional
    public DispositivoCaixa vincularDispositivo(PdvCaixa caixa) {
        if (caixa == null) {
            throw new IllegalArgumentException("Caixa é obrigatório.");
        }

        String hostname = dispositivoInfo.getHostname();
        DispositivoCaixa dispositivo = dispositivoRepository
                .findAtivoByHostname(hostname)
                .orElseGet(DispositivoCaixa::new);

        dispositivo.setCaixa(caixa);
        dispositivo.setHostname(hostname);
        dispositivo.setMacAddress(dispositivoInfo.getMacAddress());
        dispositivo.setSoNome(dispositivoInfo.getSoNome());
        dispositivo.setAtivo("S");

        if (dispositivo.getDataVinculo() == null) {
            dispositivo.setDataVinculo(LocalDateTime.now());
        }
        dispositivo.setUltimoLogin(LocalDateTime.now());

        DispositivoCaixa salvo = dispositivoRepository.save(dispositivo);
        LOGGER.info("Dispositivo {} vinculado ao caixa {}", hostname, caixa.getNome());
        return salvo;
    }

    /**
     * Atualiza o timestamp do último login para o dispositivo atual.
     */
    @Transactional
    public void registrarUltimoLogin() {
        identificarDispositivoAtual().ifPresent(d -> {
            d.setUltimoLogin(LocalDateTime.now());
            dispositivoRepository.save(d);
        });
    }
}